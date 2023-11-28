package com.pelayo.integrations.sisnetvad.helpers

import com.pelayo.integrations.common.utils.CommonFunctions
import com.pelayo.integrations.sisnetvad.utils.SisnetVadConstants
import com.pelayo.integrations.common.utils.CommonConstants

/*
 * Inicialización requerida para la ejecución de SISNetVAD
 */
def sisnetVadInit(){
	//Obtenemos el MD5 del archivo creado
	def md5SisnetVadWarLocal
	if(env.publishWar=="true"){
		md5SisnetVadWarLocal=sh(returnStdout: true, script:"md5sum ./${env.workspaceSisnetVadRelativeDirArtifact}/vad.war").tokenize('  ')[0]
	}
    def servidores=SisnetVadConstants.ENTORNOS_SISNETVAD."${env.TARGET_ENVIRONMENT}".SERVERS
    //Copia de archivos de configuración
    servidores.each{server ->
        SisnetVadConstants.ENTORNOS_SISNETVAD."${env.TARGET_ENVIRONMENT}".FILESCONFIG.each{configFile ->
            //println("## CMD: sh(\"scp ./${env.workspaceSisnetVadRelativeDirConfig}/${SisnetVadConstants.ENTORNOS_SISNETVADCONFIG.MASTER.PATH_CONFIG."${env.TARGET_ENVIRONMENT}"}/${configFile} ${server.USER}@${server.IP}:${SisnetVadConstants.ENTORNOS_SISNETVAD."${env.TARGET_ENVIRONMENT}".FILESPATHDESTINY}/${configFile}\")")
			sh("scp ./${env.workspaceSisnetVadRelativeDirConfig}/${SisnetVadConstants.ENTORNOS_SISNETVADCONFIG.MASTER.PATH_CONFIG."${env.TARGET_ENVIRONMENT}"}/${configFile} ${server.USER}@${server.IP}:${SisnetVadConstants.ENTORNOS_SISNETVAD."${env.TARGET_ENVIRONMENT}".FILESPATHDESTINY}/${configFile}")
        }

		//println("## CMD: sh(\"ssh -o StrictHostKeyChecking=no -o UserKnownHostsFile=/dev/null ${server.USER}@${server.IP} ./stopTomcat.sh\")")
		sh("ssh -o StrictHostKeyChecking=no -o UserKnownHostsFile=/dev/null ${server.USER}@${server.IP} ./stopTomcat.sh")
		//println("## CMD: sh(\"ssh -o StrictHostKeyChecking=no -o UserKnownHostsFile=/dev/null ${server.USER}@${server.IP} sleep 10 \")")
		sh("ssh -o StrictHostKeyChecking=no -o UserKnownHostsFile=/dev/null ${server.USER}@${server.IP} sleep 10 ")

		if(env.publishWar=="true"){
			//println("## CMD: sh(\"ssh -o StrictHostKeyChecking=no -o UserKnownHostsFile=/dev/null ${server.USER}@${server.IP} rm -rf /netijam/apache-tomcat/webapps/vad.war\")")
			sh("ssh -o StrictHostKeyChecking=no -o UserKnownHostsFile=/dev/null ${server.USER}@${server.IP} rm -rf /netijam/apache-tomcat/webapps/vad.war")
			//println("## CMD: sh(\"ssh -o StrictHostKeyChecking=no -o UserKnownHostsFile=/dev/null ${server.USER}@${server.IP} rm -rf /netijam/apache-tomcat/webapps/vad/\")")
			sh("ssh -o StrictHostKeyChecking=no -o UserKnownHostsFile=/dev/null ${server.USER}@${server.IP} rm -rf /netijam/apache-tomcat/webapps/vad/")
			//println("## CMD: sh(\"rsync -avz -e \"ssh -o StrictHostKeyChecking=no -o UserKnownHostsFile=/dev/null\" --progress ./${env.workspaceSisnetVadRelativeDirArtifact}/vad.war ${server.USER}@${server.IP}:/netijam/apache-tomcat/webapps/vad.war\")")
			sh("rsync -avz -e \"ssh -o StrictHostKeyChecking=no -o UserKnownHostsFile=/dev/null\" --progress ./${env.workspaceSisnetVadRelativeDirArtifact}/vad.war ${server.USER}@${server.IP}:/netijam/apache-tomcat/webapps/vad.war")
			// Verificamos que el archivo copiado al servidor es correcto validando su MD5
			def md5SisnetVadWarRemote = sh(returnStdout: true, script:"ssh -o StrictHostKeyChecking=no -o UserKnownHostsFile=/dev/null ${server.USER}@${server.IP} md5sum /netijam/apache-tomcat/webapps/vad.war").tokenize('  ')[0]
			if(md5SisnetVadWarLocal!=md5SisnetVadWarRemote){
				error("## ERROR: No se ha copiado correctamente el archivo \"vad.war\" al servidor ${server.NAME} del entorno ${SisnetVadConstants.ENTORNOS_SISNETVAD."$env.TARGET_ENVIRONMENT".NAME}, hash no válido")
			} else {
				println("## INFO: Archivo \"vad.war\" copiado correctamente al servidor ${server.NAME} del entorno ${SisnetVadConstants.ENTORNOS_SISNETVAD."$env.TARGET_ENVIRONMENT".NAME}")
			}
		}
        // Obtenemos el número de lineas del archivo de log del tomcat (actualmente parado)
        Fincata = sh(returnStdout: true, script:"ssh -o StrictHostKeyChecking=no -o UserKnownHostsFile=/dev/null ${server.USER}@${server.IP} cat /netijam/apache-tomcat/logs/catalina.out | wc -l").trim()
        println("## INFO: La última línea del fichero catalina.out, después de pararlo es: $Fincata")

		sh("ssh -o StrictHostKeyChecking=no -o UserKnownHostsFile=/dev/null ${server.USER}@${server.IP} ./arranca_sisnetvad")

        //println("## CMD: sh(\"ssh -o StrictHostKeyChecking=no -o UserKnownHostsFile=/dev/null ${server.USER}@${server.IP} /etc/init.d/maestro start\")")
        sh("sleep 7")
		sisnetPid = sh(returnStdout: true, script:"ssh -o StrictHostKeyChecking=no -o UserKnownHostsFile=/dev/null ${server.USER}@${server.IP} pgrep -f  netijam/jdk | { grep -v grep || true; }").trim()
		println("El Pid del proceso es: ${sisnetPid}")
		control = 0
		while (!(sisnetPid.length()>1) && (control<7) ) {
			control++
			println("## ERROR: Fallo en el script de arranque (arranca_sisnetvad) de SISnetVAD en el servidor $server.NAME del entorno $env.TARGET_ENVIRONMENT.")
			sh("ssh -o StrictHostKeyChecking=no -o UserKnownHostsFile=/dev/null ${server.USER}@${server.IP} ./arranca_sisnetvad")
			sh("sleep 7")
			sisnetPid = sh(returnStdout: true, script:"ssh -o StrictHostKeyChecking=no -o UserKnownHostsFile=/dev/null ${server.USER}@${server.IP} pgrep -f  netijam/jdk | { grep -v grep || true; }").trim()
			println("El Pid del proceso es: ${sisnetPid}")
		}		
		sh("sleep 130")
        Log = sh(returnStdout: true, script:"ssh -o StrictHostKeyChecking=no -o UserKnownHostsFile=/dev/null ${server.USER}@${server.IP} tail -n +$Fincata /netijam/apache-tomcat/logs/catalina.out").trim()
        Taller =  Log.toLowerCase().contains("taller de productos precargado")
        Startup = Log.toLowerCase().contains("server startup in")
        if (Taller && Startup) {
            println("## INFO: Se ha desplegado correctamente SISnetVAD.")
        } else {
            println("## ERROR: Fallo en el despliegue de SISnetVAD en el servidor $server.NAME del entorno $env.TARGET_ENVIRONMENT.")
			sh("ssh -o StrictHostKeyChecking=no -o UserKnownHostsFile=/dev/null ${server.USER}@${server.IP} ./stopTomcat.sh")
			sh("sleep 10")
			sh("ssh -o StrictHostKeyChecking=no -o UserKnownHostsFile=/dev/null ${server.USER}@${server.IP} ./arranca_sisnetvad")
			sh("sleep 150")
			Log = sh(returnStdout: true, script:"ssh -o StrictHostKeyChecking=no -o UserKnownHostsFile=/dev/null ${server.USER}@${server.IP} tail -n +$Fincata /netijam/apache-tomcat/logs/catalina.out").trim()
			Taller =  Log.toLowerCase().contains("taller de productos precargado")
			Startup = Log.toLowerCase().contains("server startup in")
			if (Taller && Startup) {
				println("## INFO: Se ha desplegado correctamente SISnetVAD.")
			} else {
				emailext (
					subject: "Job execution failed: Job '${env.JOB_NAME} [${env.BUILD_NUMBER}]'",
					body: """<p>Job '${env.JOB_NAME} [${env.BUILD_NUMBER}]':</p>
					<p>Environment configuration for environment [${env.Entorno}] </p>""",
					from: "jenkins@pelayo.com",
					to: "gnieto@pelayo.com"
				)
				error("## ERROR: Fallo en el despliegue de SISnetVAD en el servidor $server.NAME del entorno $env.TARGET_ENVIRONMENT.")
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