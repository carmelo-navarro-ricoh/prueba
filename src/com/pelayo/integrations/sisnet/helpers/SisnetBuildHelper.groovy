package com.pelayo.integrations.sisnet.helpers

import com.pelayo.integrations.sisnet.utils.SisnetConstants
import com.pelayo.integrations.common.utils.CommonFunctions
import static groovy.json.JsonOutput.*

def sisnetInit(){
    def existeWar
    //Creamos la carpeta de resources si es necesario
    if( !fileExists("./${env.workspaceSisnetRelativeDirSrc}/WebSISnetPelayo/src/main/resources") ) {
        sh("mkdir -p ./${env.workspaceSisnetRelativeDirSrc}/WebSISnetPelayo/src/main/resources")
    }
    //Ejecutamos los comandos específicos del entorno ONLINE previos a la compilación
    if(env.DEPLOYIN.toUpperCase()=="ONLINE"||env.DEPLOYIN.toUpperCase()=="BOTH"){
        sh( "scp ${env.WORKSPACE}/${SisnetConstants.uriConfigFiles}${SisnetConstants.ENTORNOS_SISNET."$env.TARGET_ENVIRONMENT".VERSION_PATH}/version.xml ${env.WORKSPACE}/${env.workspaceSisnetRelativeDirSrc}/WebSISnetPelayo/src/main/resources/version.xml")
    }
    switch(env.TARGET_ENVIRONMENT.toUpperCase()) {
        case ["INTG","PREP"]:
            println("## INFO: En entorno de ${SisnetConstants.ENTORNOS_SISNET."$env.TARGET_ENVIRONMENT".NAME}, se realiza la copia de la configuración")
            // Adecuación de archivos previo a la compilación
			if(env.DEPLOYIN.toUpperCase()=="ONLINE"||env.DEPLOYIN.toUpperCase()=="BOTH"){
				sh("scp ${env.WORKSPACE}/${SisnetConstants.uriConfigFiles}${SisnetConstants.ENTORNOS_SISNET."$env.TARGET_ENVIRONMENT".VERSION_PATH}/web.xml ${env.WORKSPACE}/${env.workspaceSisnetRelativeDirSrc}/WebSISnetPelayo/www/WEB-INF/web.xml")
				copyPomFilesToCompilePath()
				complieSisnet()
				moveJarsWar()
			}
            break
		case ["ACPT","PROD"]:
            println("## INFO: En entorno de ${SisnetConstants.ENTORNOS_SISNET."$env.TARGET_ENVIRONMENT".NAME}, se realiza la copia de la configuración")
            // Adecuación de archivos previo a la compilación
			if(env.DEPLOYIN.toUpperCase()=="ONLINE"||env.DEPLOYIN.toUpperCase()=="BOTH"){
				sh("scp ${env.WORKSPACE}/${SisnetConstants.uriConfigFiles}${SisnetConstants.ENTORNOS_SISNET."$env.TARGET_ENVIRONMENT".VERSION_PATH}/web.xml ${env.WORKSPACE}/${env.workspaceSisnetRelativeDirSrc}/WebSISnetPelayo/www/WEB-INF/web.xml")
				copyPomFilesToCompilePath()
				complieSisnet()
				moveJarsWar()
			}
			if(env.DEPLOYIN.toUpperCase()=="BATCH"||env.DEPLOYIN.toUpperCase()=="BOTH"){
				sh("scp ${env.WORKSPACE}/${SisnetConstants.uriConfigFiles}${SisnetConstants.ENTORNOS_SISNET."$env.TARGET_ENVIRONMENT".VERSION_PATH}/batch/web.xml ${env.WORKSPACE}/${env.workspaceSisnetRelativeDirSrc}/WebSISnetPelayo/www/WEB-INF/web.xml")
				copyPomFilesToCompilePath()
				complieSisnet()
				moveJarsWarBatch()
			}
            break
        default:
            error("## ERROR: No se ha definido un entorno válido")
            break
    }
}

def copyPomFilesToCompilePath(){
    // Limpieza de archivos POM anteriores
    sh "rm -rf ${env.workspaceSisnetRelativeDirSrc}/pom.xml"
    SisnetConstants.ficherosConfiguracionCompilacionModulos.each{ fichero ->
        sh("rm -rf ${env.workspaceSisnetRelativeDirSrc}/$fichero")
    }
    // Copia de nuevos POM
    sh("cp ${WORKSPACE}/${SisnetConstants.uriMavenFiles}${SisnetConstants.ENTORNOS_SISNET."$env.TARGET_ENVIRONMENT".VERSION_PATH}/SISnetPelayoParent/pom.xml ${env.WORKSPACE}/${env.workspaceSisnetRelativeDirSrc}/pom.xml")
    sh("cp ${WORKSPACE}/${SisnetConstants.uriMavenFiles}${SisnetConstants.ENTORNOS_SISNET."$env.TARGET_ENVIRONMENT".VERSION_PATH}/IntegracionesPelayo/pom.xml ${env.WORKSPACE}/${env.workspaceSisnetRelativeDirSrc}/IntegracionesPelayo/pom.xml")
    sh("cp ${WORKSPACE}/${SisnetConstants.uriMavenFiles}${SisnetConstants.ENTORNOS_SISNET."$env.TARGET_ENVIRONMENT".VERSION_PATH}/SISnetPelayo/pom.xml ${env.WORKSPACE}/${env.workspaceSisnetRelativeDirSrc}/SISnetPelayo/pom.xml")
    sh("cp ${WORKSPACE}/${SisnetConstants.uriMavenFiles}${SisnetConstants.ENTORNOS_SISNET."$env.TARGET_ENVIRONMENT".VERSION_PATH}/SISnetPelayoVAD/pom.xml ${env.WORKSPACE}/${env.workspaceSisnetRelativeDirSrc}/SISnetPelayoVAD/pom.xml")
    sh("cp ${WORKSPACE}/${SisnetConstants.uriMavenFiles}${SisnetConstants.ENTORNOS_SISNET."$env.TARGET_ENVIRONMENT".VERSION_PATH}/WebSISnetPelayo/pom.xml ${env.WORKSPACE}/${env.workspaceSisnetRelativeDirSrc}/WebSISnetPelayo/pom.xml")
    //pomVersionChange("${env.WORKSPACE}/${env.workspaceSisnetRelativeDirSrc}/pom.xml", env.ARTIFACT_VERSION)
    pomVersionChange("${env.WORKSPACE}/${env.workspaceSisnetRelativeDirSrc}/pom.xml", env.ARTIFACT_VERSION)
    pomParentVersionChange("${env.WORKSPACE}/${env.workspaceSisnetRelativeDirSrc}/IntegracionesPelayo/pom.xml", env.ARTIFACT_VERSION)
    pomParentVersionChange("${env.WORKSPACE}/${env.workspaceSisnetRelativeDirSrc}/SISnetPelayo/pom.xml", env.ARTIFACT_VERSION)
    pomParentVersionChange("${env.WORKSPACE}/${env.workspaceSisnetRelativeDirSrc}/SISnetPelayoVAD/pom.xml", env.ARTIFACT_VERSION)
    pomParentVersionChange("${env.WORKSPACE}/${env.workspaceSisnetRelativeDirSrc}/WebSISnetPelayo/pom.xml", env.ARTIFACT_VERSION)
}

def complieSisnet(){
    //Compilación
    def mvnHome = tool 'maven 3.3.9'
    env.PATH_MAVEN_3_3_9 = mvnHome + "/bin/mvn"
    dir("./${env.workspaceSisnetRelativeDirSrc}") {
        sh("${env.PATH_MAVEN_3_3_9} clean install -U")
    }
}

def moveJarsWar(){
	//Dejamos los jar y war en la carpeta de almacén de artefactos
	sh("mkdir -p ./${env.workspaceSisnetRelativeDirArtifact}/batch")
	sh("cp ${env.workspaceSisnetRelativeDirSrc}/IntegracionesPelayo/target/IntegracionesPelayo-${env.ARTIFACT_VERSION}.jar ./${env.workspaceSisnetRelativeDirArtifact}/${SisnetConstants.artifacts.INTEGRACIONES}-${env.ARTIFACT_VERSION}.jar")
	sh("cp ${env.workspaceSisnetRelativeDirSrc}/SISnetPelayo/target/SISnetPelayo-${env.ARTIFACT_VERSION}.jar ./${env.workspaceSisnetRelativeDirArtifact}/${SisnetConstants.artifacts.SISNET}-${env.ARTIFACT_VERSION}.jar")
	sh("cp ${env.workspaceSisnetRelativeDirSrc}/SISnetPelayoVAD/target/SISnetPelayoVAD-${env.ARTIFACT_VERSION}.jar ./${env.workspaceSisnetRelativeDirArtifact}/${SisnetConstants.artifacts.SISNETVAD}-${env.ARTIFACT_VERSION}.jar")
	//Mover el archivo sisnet.war al workspace y posible modificación del nombre (revisar)
	sh("cp ${WORKSPACE}/${env.workspaceSisnetRelativeDirSrc}/WebSISnetPelayo/target/sisnet.war ./${env.workspaceSisnetRelativeDirArtifact}/sisnet.war")
}

def moveJarsWarBatch(){
	//Dejamos los jar y war en la carpeta de almacén de artefactos
	sh("mkdir -p ./${env.workspaceSisnetRelativeDirArtifact}/batch")
	sh("cp ${env.workspaceSisnetRelativeDirSrc}/IntegracionesPelayo/target/IntegracionesPelayo-${env.ARTIFACT_VERSION}.jar ./${env.workspaceSisnetRelativeDirArtifact}/batch/${SisnetConstants.artifacts.INTEGRACIONES}-${env.ARTIFACT_VERSION}.jar")
	sh("cp ${env.workspaceSisnetRelativeDirSrc}/SISnetPelayo/target/SISnetPelayo-${env.ARTIFACT_VERSION}.jar ./${env.workspaceSisnetRelativeDirArtifact}/batch/${SisnetConstants.artifacts.SISNET}-${env.ARTIFACT_VERSION}.jar")
	sh("cp ${env.workspaceSisnetRelativeDirSrc}/SISnetPelayoVAD/target/SISnetPelayoVAD-${env.ARTIFACT_VERSION}.jar ./${env.workspaceSisnetRelativeDirArtifact}/batch/${SisnetConstants.artifacts.SISNETVAD}-${env.ARTIFACT_VERSION}.jar")
	//Mover el archivo sisnet.war al workspace y posible modificación del nombre (revisar)
	sh("cp ${WORKSPACE}/${env.workspaceSisnetRelativeDirSrc}/WebSISnetPelayo/target/sisnet.war ./${env.workspaceSisnetRelativeDirArtifact}/batch/sisnet.war")
}

def pomParentVersionChange(String file, String newVersion){
    def pom = readMavenPom file: "${file}"
    pom.parent.version=newVersion
    writeMavenPom model: pom, file: "${file}"
}

def pomVersionChange(String file, String newVersion){
    def pom = readMavenPom file: "${file}"
    pom.version=newVersion
    writeMavenPom model: pom, file: "${file}"
}

/**
* Enviar evidencia a la cmdb.
* @param jenkinsStatus estado del la tarea de jenkins a registrar
* @deployStatus estado de la aplicacion una vez desplegada
*/
def sendToCmdb (jenkinsStatus,deployStatus) {   
    new CommonFunctions().notifyJobCMDB(jenkinsStatus,deployStatus,env.ARTIFACT_VERSION,"build");
}
