package com.pelayo.integrations.sisnetv6.helpers

import com.pelayo.integrations.sisnetv6.utils.SisnetV6Constants
import com.pelayo.integrations.sisnetv6.utils.SisnetV6Functions
import com.pelayo.integrations.common.utils.CommonFunctions
import com.pelayo.integrations.common.utils.xmlsql


/*
 * Inicialización requerida para la ejecución de SISNet
 */
def sisnetV6Init(){
    if (env.TAG_TO_BUILD) {
        new CommonFunctions().downloadArtifactFromNexus(
            "${SisnetV6Constants.artifactRepository."${env.TARGET_ENVIRONMENT}".URL}",
            "${SisnetV6Constants.artifactRepository."${env.TARGET_ENVIRONMENT}".REPOSITORY}",
            "${env.ARTIFACT_GROUPID.replaceAll('\\.','/')}",
            "${env.ARTIFACT_NAME}", 
            "${env.TAG_TO_BUILD}", 
            "${env.ARTIFACT_NAME}-${env.TAG_TO_BUILD}.war",
            SisnetV6Constants.NEW_WAR_PATH,
            SisnetV6Constants.NEW_WAR_FILE)
    }

    if(env.TARGET_ENVIRONMENT!="INTG"){
        makeFlyWayLogic()
    }
    executeDeploy(env.TARGET_ENVIRONMENT)
}

def makeFlyWayLogic() {
    //Lectura de información de la BBDD de sisnet
        def dbProperties=new CommonFunctions().readDBProperties()
        println("DBProperties:\n${dbProperties}")
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
            scriptsToExecute.each{
                switch(it.TYPE) {
                    case ["SQL"]:   //Ejecución de los SQLs
                        def tmpValues=""
                        withCredentials([usernamePassword(credentialsId: "jenkins_to_oracle_${env.APPNAME.toLowerCase()}_${env.TARGET_ENVIRONMENT.toLowerCase()}_${it.SCHEMA}", usernameVariable: 'USERNAME', passwordVariable: 'PASSWORD')]){
                            withCredentials([string(credentialsId: "jenkins_to_oracle_${env.APPNAME.toLowerCase()}_${env.TARGET_ENVIRONMENT.toLowerCase()}_JDBC", variable: 'SECRET')]){
                                def sqlPath="${env.WORKSPACE}/${it.PATH}"
                                //Muestra la información del flyway, historico de ejecuciones.
                                configFileProvider([configFile(fileId: env.ID_CUSTOM_MAVEN_SETTINGS, variable: 'MAVEN_SETTINGS_XML')]) {
                                    sh script: env.PATH_MAVEN_3_3_9 + ' flyway:info -Dflyway.password="'+PASSWORD+' -Dflyway.user="'+USERNAME+'" -Dflyway.url="'+SECRET+'" -Dflyway.locations=filesystem:"'+sqlPath+'" -gs '+MAVEN_SETTINGS_XML
                                }
                                //Ejecuta una revisión de los sqls ejecutados y los repara a la vez que crea una nueva "linea base" y ejecuta los scripts aunque se hayan ejecutado ya antes.
                                //sh script:"mvn flyway:repair -Dflyway.password="+PASSWORD+" -Dflyway.user="+USERNAME+" -Dflyway.url="+SECRET+" -Dflyway.locations=filesystem:"+sqlPath
                                //sh("ls ${sqlPath} | grep .[sS][qQ][lL]")
                                def fileList = sh(returnStdout: true, script:"ls ${sqlPath} | grep .[sS][qQ][lL]").split("\\r?\\n")
                                try {
                                    def response
                                    configFileProvider([configFile(fileId: env.ID_CUSTOM_MAVEN_SETTINGS, variable: 'MAVEN_SETTINGS_XML')]) {
                                        response=sh(returnStdout: true, script:"mvn flyway:migrate -Dflyway.password="+PASSWORD+" -Dflyway.user="+USERNAME+" -Dflyway.url="+SECRET+" -Dflyway.locations=filesystem:"+sqlPath+" -gs "+MAVEN_SETTINGS_XML).trim()
                                    }
                                    println("## INFO: ${response}")
                                    fileList.each{
                                        def md5Check = sh(script: "md5sum ${sqlPath}/${it} | awk \'{print \$1}\'", returnStdout: true ).trim()
                                        def cmdbResultUpload = new xmlsql().cmdbFilesUpload("SISNETV6","${env.TARGET_ENVIRONMENT}","${env.BRANCH_NAME}","${it}","SQL","${md5Check}","OK",true)
                                    }
                                } catch(Exception ex) {
                                    println("## WARNING: Se ha detectado que la ejecución de los SQLs ya se habia realizado previamente")
                                    fileList.each{
                                        def md5Check = sh(script: "md5sum ${sqlPath}/${it} | awk \'{print \$1}\'", returnStdout: true ).trim()
                                        def cmdbResultUpload = new xmlsql().cmdbFilesUpload("SISNETV6","${env.TARGET_ENVIRONMENT}","${env.BRANCH_NAME}","${it}","SQL","${md5Check}","KO",true)
                                    }
                                }
                            }
                        }
                    break
                    default:
                        println("## WARNING: No se ha especificado un valor del archivo válido, ha de ser \"SQL\"")
                    break
                }
            }
        }
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

def executeDeploy(environment){
    if (environment == SisnetV6Constants.environments.PROD.TARGET &&
        !env.TAG_TO_BUILD) {
            // Quiere decir que se está ejecutando la rama master con el multibranch.
        println("No se realiza despliegue en el entorno de producción.")
        return
    }

    // Aquí solo entra si se está ejecutando el promote to pro.
    SisnetV6Constants.deployCommands."${environment}".SERVERS.each{ SERVER ->
        SisnetV6Constants.deployCommands."${environment}".COMMANDS.each{ COMMAND ->
            COMMAND=COMMAND.replace("##USER##", SERVER.USER)
            COMMAND=COMMAND.replace("##SERVER##", SERVER.SERVER)
            //println(COMMAND)
            sh("${COMMAND}")
        }
    }
}