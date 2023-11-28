package com.pelayo.integrations.sisnet.helpers

import com.pelayo.integrations.common.utils.CommonFunctions
import com.pelayo.integrations.common.utils.xmlsql
import com.pelayo.integrations.sisnet.utils.SisnetConstants
import com.pelayo.integrations.sisnet.utils.SisnetFunctions

/*
 * Inicialización requerida para la ejecución de SISNet
 */
def sisnetInit(){
    //Obtenemos el MD5 del archivo creado
    def md5SisnetWarLocalOnline
	def md5SisnetWarLocalBatch
    if(env.publishWar=="true"){
        switch(env.DEPLOYIN.toUpperCase()) {
			case ["ONLINE"]:
				md5SisnetWarLocalOnline=sh(returnStdout: true, script:"md5sum ./${env.workspaceSisnetRelativeDirArtifact}/sisnet.war").tokenize('  ')[0]
				break
			case ["BATCH"]:
				md5SisnetWarLocalBatch=sh(returnStdout: true, script:"md5sum ./${env.workspaceSisnetRelativeDirArtifact}/batch/sisnet.war").tokenize('  ')[0]
				break
			case ["BOTH"]:
				if((env.TARGET_ENVIRONMENT.toUpperCase()=="INTG" || env.TARGET_ENVIRONMENT.toUpperCase()=="PREP")){
					md5SisnetWarLocalOnline=sh(returnStdout: true, script:"md5sum ./${env.workspaceSisnetRelativeDirArtifact}/sisnet.war").tokenize('  ')[0]
				} else {
					md5SisnetWarLocalOnline=sh(returnStdout: true, script:"md5sum ./${env.workspaceSisnetRelativeDirArtifact}/sisnet.war").tokenize('  ')[0]
					md5SisnetWarLocalBatch=sh(returnStdout: true, script:"md5sum ./${env.workspaceSisnetRelativeDirArtifact}/batch/sisnet.war").tokenize('  ')[0]
				}
				break
			default:
				error("## ERROR: No se ha definido correctamente en el archivo ci.properties el valor de \"DeployIn\"")
				break
		}
    }
    //Variables a usar en toda la función
    def servidores
    //Seleccionamos los archivos de configuración del entorno
    def ficheros=SisnetConstants.ENTORNOS_SISNET."$env.TARGET_ENVIRONMENT".CONFIG_FILE_LIST

    //Seleccionamos los servidores sobre los que se van a ejecutar los comandos
    switch(env.DEPLOYIN.toUpperCase()) {
        case ["ONLINE","BATCH"]:
            servidores=SisnetConstants.ENTORNOS_SISNET."$env.TARGET_ENVIRONMENT".SERVERS.findAll{it."${env.DEPLOYIN.toUpperCase()}"}
            break
        case ["BOTH"]:
			if((env.TARGET_ENVIRONMENT.toUpperCase()=="INTG" || env.TARGET_ENVIRONMENT.toUpperCase()=="PREP")){
				servidores=SisnetConstants.ENTORNOS_SISNET."$env.TARGET_ENVIRONMENT".SERVERS.findAll{it.ONLINE}
			} else {
				servidores=SisnetConstants.ENTORNOS_SISNET."$env.TARGET_ENVIRONMENT".SERVERS
			}
            break
        default:
            error("## ERROR: No se ha definido correctamente en el archivo ci.properties el valor de \"DeployIn\"")
            break
    }
    //Verificando si existen servidores de destino de los archivos de configuración.
    if(servidores.size()<1){
        error("## ERROR: No existen servidores para el entorno $env.TARGET_ENVIRONMENT del tipo ${env.DEPLOYIN}")
    }

    //Lectura de información de la BBDD de sisnet
    def dbProperties=new CommonFunctions().readDBProperties()
    def dbPropertiesValid=[]
    //Verificamos si existen todos los branches a utilizar
    dbProperties.each{
        def resultado=new CommonFunctions().checkRemoteBranchExist("${it.REPO}","${it.BRANCH}",true)
        if(resultado){
            dbPropertiesValid.add(it)
        } else {
            println("## WARNING: El branch ${it.BRANCH} del repositorio ${it.REPO} no existe, se excluye del proceso.")
        }
    }

    if(env.TARGET_ENVIRONMENT!="INTG"){
        makeFlyWayLogic(dbPropertiesValid)
    }
    deploySisnet(ficheros, servidores, md5SisnetWarLocalOnline, md5SisnetWarLocalBatch)
}

def makeFlyWayLogic(dbPropertiesValid) {
    def infoTest="""#######################################################
## Repositorio/s de mantenimiento de BBDD a ejecutar ##
#######################################################"""
        dbPropertiesValid.each{
            infoTest+="\n## REPOSITORIO: ${it.REPO}"
            infoTest+="\n## BRANCH: ${it.BRANCH}"
            infoTest+="\n#######################################################"
        }
        println(infoTest)
        dbPropertiesValid.each{
            new CommonFunctions().checkoutGit(it.REPO, it.BRANCH, "./db_maintenance")
            def dbScripts=new CommonFunctions().readDBScripts()
            //Mostrar la información de los scripts que se han seleccionado para ejecutar.
            def infoScripts="""##################################################
## Script/s de mantenimiento de BBDD a ejecutar ##
##################################################"""
            dbScripts.each{
                infoScripts+="\n## FICHERO: ${it.FILE}"
                infoScripts+="\n## TIPO: ${it.TYPE}"
                infoScripts+="\n## ESQUEMA: ${it.SCHEMA}"
                infoScripts+="\n## ORDEN: ${it.ORDER}"
                infoScripts+="\n##################################################"
            }
            println(infoScripts)
            def scriptsToExecute=new xmlsql().orderDBFiles(dbScripts,"./db_maintenance")
            def mvnHome = tool 'maven 3.6.0'
            def mvnExec = mvnHome + "/bin/mvn"
            scriptsToExecute.each{
                switch(it.TYPE) {
                    case ["SQL"]:   //Ejecución de los SQLs
                        def tmpValues=""
                        withCredentials([usernamePassword(credentialsId: "jenkins_to_oracle_${env.APPNAME.toLowerCase()}_${env.TARGET_ENVIRONMENT.toLowerCase()}_${it.SCHEMA}", usernameVariable: 'USERNAME', passwordVariable: 'PASSWORD')]){
                            withCredentials([string(credentialsId: "jenkins_to_oracle_${env.APPNAME.toLowerCase()}_${env.TARGET_ENVIRONMENT.toLowerCase()}_JDBC", variable: 'SECRET')]){
                                configFileProvider([configFile(fileId: env.ID_CUSTOM_MAVEN_SETTINGS, variable: 'MAVEN_SETTINGS_XML')]) {
                                    dir("./${env.workspaceSisnetRelativeDirSrc}"){
                                        def sqlPath="${env.WORKSPACE}/${it.PATH}"
                                        //Muestra la información del flyway, historico de ejecuciones.
                                        //sh script:"mvn flyway:info -Dflyway.password="+PASSWORD+" -Dflyway.user="+USERNAME+" -Dflyway.url="+SECRET+" -Dflyway.locations=filesystem:"+sqlPath
                                        //Ejecuta una revisión de los sqls ejecutados y los repara a la vez que crea una nueva "linea base" y ejecuta los scripts aunque se hayan ejecutado ya antes.
                                        //sh script:"mvn flyway:repair -Dflyway.password="+PASSWORD+" -Dflyway.user="+USERNAME+" -Dflyway.url="+SECRET+" -Dflyway.locations=filesystem:"+sqlPath
                                        sh("ls ${sqlPath} | grep .[sS][qQ][lL]")
                                        def fileList = sh(returnStdout: true, script:"ls ${sqlPath} | grep .[sS][qQ][lL]").split("\\r?\\n")
                                        try {
                                            def response=sh(returnStdout: true, script: env.PATH_MAVEN_3_3_9 + ' flyway:migrate -Dflyway.password="'+PASSWORD+'" -Dflyway.user="'+USERNAME+'" -Dflyway.url="'+SECRET+'" -Dflyway.locations=filesystem:'+sqlPath).trim()
                                            fileList.each{
                                                def md5Check = sh(script: "md5sum ${sqlPath}/${it} | awk \'{print \$1}\'", returnStdout: true ).trim()
                                                def cmdbResultUpload = new xmlsql().cmdbFilesUpload("SISNET","${env.TARGET_ENVIRONMENT}","${env.BRANCH_NAME}","${it}","SQL","${md5Check}","OK",true)
                                            }
                                        } catch(Exception ex) {
                                            println("Excepcion: ${ex}")
                                            println("## WARNING: Se ha detectado que la ejecución de los SQLs ya se habia realizado previamente")
                                            fileList.each{
                                                def md5Check = sh(script: "md5sum ${sqlPath}/${it} | awk \'{print \$1}\'", returnStdout: true ).trim()
                                                def cmdbResultUpload = new xmlsql().cmdbFilesUpload("SISNET","${env.TARGET_ENVIRONMENT}","${env.BRANCH_NAME}","${it}","SQL","${md5Check}","KO",true)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    break
                    case ["XML"]:
                        println("## INFO: Ejecución del XML Complejo que se han de incluir en el proceso de despliegue de SISnet")
                        if(SisnetConstants.ENTORNOS_SISNET."${env.TARGET_ENVIRONMENT}".XMLCOMPLEJO.ACTIVE) {
                            //Calculamos el MD5 del archivo a ejecutar
                            def md5Check = sh( script: "md5sum ./${it.PATH}/${it.FILE} | awk \'{print \$1}\'", returnStdout: true ).trim()
                            //Buscamos el archivo a ejecutar en la BBDD
                            def cmdbResultSearch = new xmlsql().cmdbFilesSearch("SISNET","${env.TARGET_ENVIRONMENT}","${env.BRANCH_NAME}","${it.FILE}","XML",true)
                            def executeXML=true
                            //Verificamos si ya se ha ejecutado el archivo anteriormente
                            if(cmdbResultSearch.totalElements!=0){
                                //Verificamos si el archivo a tenido alguna modificación
                                if(cmdbResultSearch.content[0].hash!=md5Check && cmdbResultSearch.content[0].result!="OK") {
                                    executeXML==false
                                }
                            }
                            if(executeXML){
                                def resultadoUploadXML=new xmlsql().uploadXML("${SisnetConstants.ENTORNOS_SISNET."${env.TARGET_ENVIRONMENT}".XMLCOMPLEJO.URL}","./${it.PATH}/${it.FILE}","${env.TARGET_ENVIRONMENT}",true)
                                if(resultadoUploadXML){
                                    def cmdbResultUpload = new xmlsql().cmdbFilesUpload("SISNET","${env.TARGET_ENVIRONMENT}","${env.BRANCH_NAME}","${it.FILE}","XML","${md5Check}","OK",true)
                                } else {
                                    def cmdbResultUpload = new xmlsql().cmdbFilesUpload("SISNET","${env.TARGET_ENVIRONMENT}","${env.BRANCH_NAME}","${it.FILE}","XML","${md5Check}","KO",true)
                                }
                            } else {
                                println("## WARNING: No se ejecuta el script ${it.FILE} al haber sido detectada su ejecución anterior en la CMDB")
                            }
                        }
                    break
                    default:
                        println("## WARNING: No se ha especificado un valor del archivo válido, ha de ser \"XML\" o \"SQL\"")
                    break
                }
            }
        }
}

def deploySisnet(ficheros, servidores, md5SisnetWarLocalOnline, md5SisnetWarLocalBatch) {
    //Copia de archivos a los servidores finales
    copyConfigFilesToServers(ficheros,servidores)

    //Generamos las variables específicas de cada entorno
    def remoteWarPath=""
    def remoteCatalinaPath=""
    switch(env.TARGET_ENVIRONMENT.toUpperCase()) {
        case ["INTG","ACPT","PREP"]:
            remoteWarPath="/netijam/apache-tomcat/webapps/sisnet.war"
            remoteCatalinaPath="/netijam/apache-tomcat/logs/catalina.out"
            break
        case ["PROD"]:
            remoteWarPath="/netijam/apache-tomcat-8.0.33/webapps/sisnet.war"
            remoteCatalinaPath="/netijam/apache-tomcat-8.0.33/logs/catalina.out"
            break
/*
        case "PROD":
            println("## INFO: En entorno de producción, el despliegue de la aplicación no está implementada")
            return
*/
        default:
            error("## ERROR: No se ha definido un entorno válido")
            break
    }

    servidores.each{server ->
        if(server.USRHOST==""){
            println("## INFO: No es posible conectarse al servidor $server.NAME ya que no se dispone de la información necesaria para su conexión.")
        } else {
            SisnetConstants.ENTORNOS_SISNET."$env.TARGET_ENVIRONMENT".COMANDOS_INICIALES_DESPLIEGUE.each{comando ->
                //println("## CMD: sh(\"ssh -o StrictHostKeyChecking=no -o UserKnownHostsFile=/dev/null ${server.USRHOST} $comando\")")
                sh("ssh -o StrictHostKeyChecking=no -o UserKnownHostsFile=/dev/null ${server.USRHOST} $comando")
            }

            if(env.publishWar=="true"){
                SisnetConstants.ENTORNOS_SISNET."$env.TARGET_ENVIRONMENT".COMANDOS_LIMPIEZA_DESPLIEGUE.each{comando ->
                    //println("## CMD: sh(\"ssh -o StrictHostKeyChecking=no -o UserKnownHostsFile=/dev/null ${server.USRHOST} $comando\")")
                    sh("ssh -o StrictHostKeyChecking=no -o UserKnownHostsFile=/dev/null ${server.USRHOST} $comando")
                }

                //println("## CMD: sh(\"rsync -avz -e \'ssh -o StrictHostKeyChecking=no -o UserKnownHostsFile=/dev/null\' --progress ./${env.workspaceSisnetRelativeDirArtifact}/sisnet.war ${server.USRHOST}:${remoteWarPath}\")")
                
				if((env.DEPLOYIN.toUpperCase()=="ONLINE"||env.DEPLOYIN.toUpperCase()=="BOTH")&&(!server.USRHOST.toLowerCase().contains("batch"))){
					sh("rsync -avz -e 'ssh -o StrictHostKeyChecking=no -o UserKnownHostsFile=/dev/null' --progress ./${env.workspaceSisnetRelativeDirArtifact}/sisnet.war ${server.USRHOST}:${remoteWarPath}")
				}
				if((env.DEPLOYIN.toUpperCase()=="BATCH"||env.DEPLOYIN.toUpperCase()=="BOTH")&&(server.USRHOST.toLowerCase().contains("batch"))){
					sh("rsync -avz -e 'ssh -o StrictHostKeyChecking=no -o UserKnownHostsFile=/dev/null' --progress ./${env.workspaceSisnetRelativeDirArtifact}/batch/sisnet.war ${server.USRHOST}:${remoteWarPath}")
				}
                
				// Verificamos que el archivo copiado al servidor es correcto validando su MD5
                if((env.DEPLOYIN.toUpperCase()=="ONLINE"||env.DEPLOYIN.toUpperCase()=="BOTH")&&(!server.USRHOST.toLowerCase().contains("batch"))){
					def md5SisnetWarRemoteOnline = sh(returnStdout: true, script:"ssh -o StrictHostKeyChecking=no -o UserKnownHostsFile=/dev/null ${server.USRHOST} md5sum ${remoteWarPath}").tokenize('  ')[0]
					if(md5SisnetWarLocalOnline!=md5SisnetWarRemoteOnline){
						error("## ERROR: No se ha copiado correctamente el archivo \"sisnet.war\" al servidor ${server.NAME} del entorno ${SisnetConstants.ENTORNOS_SISNET."$env.TARGET_ENVIRONMENT".NAME}, hash no válido")
					} else {
						println("## INFO: Archivo \"sisnet.war\" copiado correctamente al servidor ${server.NAME} del entorno ${SisnetConstants.ENTORNOS_SISNET."$env.TARGET_ENVIRONMENT".NAME}")
					}
				}
				if((env.DEPLOYIN.toUpperCase()=="BATCH"||env.DEPLOYIN.toUpperCase()=="BOTH")&&(server.USRHOST.toLowerCase().contains("batch"))){
					def md5SisnetWarRemoteBatch = sh(returnStdout: true, script:"ssh -o StrictHostKeyChecking=no -o UserKnownHostsFile=/dev/null ${server.USRHOST} md5sum ${remoteWarPath}").tokenize('  ')[0]
					if(md5SisnetWarLocalBatch!=md5SisnetWarRemoteBatch){
						error("## ERROR: No se ha copiado correctamente el archivo \"sisnet.war\" al servidor ${server.NAME} del entorno ${SisnetConstants.ENTORNOS_SISNET."$env.TARGET_ENVIRONMENT".NAME}, hash no válido")
					} else {
						println("## INFO: Archivo \"sisnet.war\" copiado correctamente al servidor ${server.NAME} del entorno ${SisnetConstants.ENTORNOS_SISNET."$env.TARGET_ENVIRONMENT".NAME}")
					}
				}
            }
        }
        // Obtenemos el número de lineas del archivo de log del tomcat (actualmente parado)
        Fincata = sh(returnStdout: true, script:"ssh -o StrictHostKeyChecking=no -o UserKnownHostsFile=/dev/null ${server.USRHOST} cat ${remoteCatalinaPath} | wc -l").trim()
        println("## INFO: La última línea del fichero catalina.out, después de pararlo es: $Fincata")
        //println("## CMD: sh(\"ssh -o StrictHostKeyChecking=no -o UserKnownHostsFile=/dev/null ${server.USRHOST} /etc/init.d/maestro start\")")
        sh("ssh -o StrictHostKeyChecking=no -o UserKnownHostsFile=/dev/null ${server.USRHOST} ./arranca_maestro")
        sh("sleep 7")
		sisnetPid = sh(returnStdout: true, script:"ssh -o StrictHostKeyChecking=no -o UserKnownHostsFile=/dev/null ${server.USRHOST} pgrep -f  netijam/jdk | { grep -v grep || true; }").trim()
		println("El Pid del proceso es: ${sisnetPid}")
		control = 0
		while (!(sisnetPid.length()>1) && (control<7) ) {
			control++
			println("## ERROR: Fallo en el script de arranque (arranca_maestro) de SISnet en el servidor $server.NAME del entorno $env.TARGET_ENVIRONMENT.")
			sh("ssh -o StrictHostKeyChecking=no -o UserKnownHostsFile=/dev/null ${server.USRHOST} ./arranca_maestro")
			sh("sleep 7")
			sisnetPid = sh(returnStdout: true, script:"ssh -o StrictHostKeyChecking=no -o UserKnownHostsFile=/dev/null ${server.USRHOST} pgrep -f  netijam/jdk | { grep -v grep || true; }").trim()
			println("El Pid del proceso es: ${sisnetPid}")
		}		
		sh("sleep 130")
        Log = sh(returnStdout: true, script:"ssh -o StrictHostKeyChecking=no -o UserKnownHostsFile=/dev/null ${server.USRHOST} tail -n +$Fincata $remoteCatalinaPath").trim()
        Taller =  Log.toLowerCase().contains("taller de productos precargado")
        Startup = Log.toLowerCase().contains("server startup in")
        if (Taller && Startup) {
            println("## INFO: Se ha desplegado correctamente SISnet.")
        } else {
            println("## ERROR: Fallo en el despliegue de SISnet en el servidor $server.NAME del entorno $env.TARGET_ENVIRONMENT.")
			sh("ssh -o StrictHostKeyChecking=no -o UserKnownHostsFile=/dev/null ${server.USRHOST} ./stopTomcat.sh")
			sh("sleep 10")
			sh("ssh -o StrictHostKeyChecking=no -o UserKnownHostsFile=/dev/null ${server.USRHOST} ./arranca_maestro")
			sh("sleep 150")
			Log = sh(returnStdout: true, script:"ssh -o StrictHostKeyChecking=no -o UserKnownHostsFile=/dev/null ${server.USRHOST} tail -n +$Fincata $remoteCatalinaPath").trim()
			Taller =  Log.toLowerCase().contains("taller de productos precargado")
			Startup = Log.toLowerCase().contains("server startup in")
			if (Taller && Startup) {
				println("## INFO: Se ha desplegado correctamente SISnet.")
			} else {
				emailext (
					subject: "Job execution failed: Job '${env.JOB_NAME} [${env.BUILD_NUMBER}]'",
					body: """<p>Job '${env.JOB_NAME} [${env.BUILD_NUMBER}]':</p>
					<p>Environment configuration for environment [${env.Entorno}] </p>""",
					from: "jenkins@pelayo.com",
					to: "gnieto@pelayo.com"
				)
				error("## ERROR: Fallo en el despliegue de SISnet en el servidor $server.NAME del entorno $env.TARGET_ENVIRONMENT.")
			}
        }
    }
}

//Copia de ficheros de configuración a los servidores de destino.
//Aprovechhamos que fichero de constantes está definido primero el servidor online.
def copyConfigFilesToServers(ficheros,servidores){
    if(servidores.size()>0 && ficheros.size()>0){
        servidores.each{server ->
            ficheros.each{fichero ->
                if((env.DEPLOYIN.toUpperCase()=="ONLINE"||env.DEPLOYIN.toUpperCase()=="BOTH")&&(!server.USRHOST.toLowerCase().contains("batch"))){
					def path_origen=construyeURIFicheroConfiguracionOrigen(env.WORKSPACE, SisnetConstants.uriConfigFiles, SisnetConstants.ENTORNOS_SISNET."$env.TARGET_ENVIRONMENT".VERSION_PATH, fichero)
					def path_destino=construyeURIFicheroConfiguracionDestino(SisnetConstants.ENTORNOS_SISNET."$env.TARGET_ENVIRONMENT".USER,server.IP,SisnetConstants.ENTORNOS_SISNET."$env.TARGET_ENVIRONMENT".REMOTE_DEPLOY_PATH,fichero)
					sh("scp ${path_origen} ${path_destino}")
				}
				if((env.DEPLOYIN.toUpperCase()=="BATCH"||env.DEPLOYIN.toUpperCase()=="BOTH")&&(server.USRHOST.toLowerCase().contains("batch"))){
					if(fichero.toUpperCase()=="SISNET.XML"||fichero.toUpperCase()=="SISNET_ENTORNO.XML"){
						def path_origen=construyeURIFicheroConfiguracionOrigenBatch(env.WORKSPACE, SisnetConstants.uriConfigFiles, SisnetConstants.ENTORNOS_SISNET."$env.TARGET_ENVIRONMENT".VERSION_PATH, fichero)
						def path_destino=construyeURIFicheroConfiguracionDestino(SisnetConstants.ENTORNOS_SISNET."$env.TARGET_ENVIRONMENT".USER,server.IP,SisnetConstants.ENTORNOS_SISNET."$env.TARGET_ENVIRONMENT".REMOTE_DEPLOY_PATH,fichero)
						sh("scp ${path_origen} ${path_destino}")
					} else {
						def path_origen=construyeURIFicheroConfiguracionOrigen(env.WORKSPACE, SisnetConstants.uriConfigFiles, SisnetConstants.ENTORNOS_SISNET."$env.TARGET_ENVIRONMENT".VERSION_PATH, fichero)
						def path_destino=construyeURIFicheroConfiguracionDestino(SisnetConstants.ENTORNOS_SISNET."$env.TARGET_ENVIRONMENT".USER,server.IP,SisnetConstants.ENTORNOS_SISNET."$env.TARGET_ENVIRONMENT".REMOTE_DEPLOY_PATH,fichero)
						sh("scp ${path_origen} ${path_destino}")
					}
				}
            }
       }
    } else {
        println("## WARNING: No se ha copiado ningún archivo en los servidores de destino ya que no hay archivos o servidores en los que copiarlos")
    }
}

//Función que retorna la ruta de origen de un archivo de configuración
def construyeURIFicheroConfiguracionOrigen(String rutaBase, String pathFiles, String pathVersion, String fichero){
	return "${rutaBase}/${pathFiles}${pathVersion}/${fichero}"
}

//Función que retorna la ruta de origen de un archivo de configuración batch
def construyeURIFicheroConfiguracionOrigenBatch(String rutaBase, String pathFiles, String pathVersion, String fichero){
	return "${rutaBase}/${pathFiles}${pathVersion}/batch/${fichero}"
}

//Función que retorna la ruta de destino de un archivo de configuración
def construyeURIFicheroConfiguracionDestino(String user, String ip, String pathDeploy, String fichero){
    return "${user}@${ip}:${pathDeploy}${fichero}"
}

/**
* Enviar evidencia a la cmdb.
* @param jenkinsStatus estado del la tarea de jenkins a registrar
* @deployStatus estado de la aplicacion una vez desplegada
*/
def sendToCmdb (jenkinsStatus,deployStatus) {   
    // llamar a la CMDB
    new CommonFunctions().notifyJobCMDB(jenkinsStatus,deployStatus,env.ARTIFACT_VERSION,"deploy");
}