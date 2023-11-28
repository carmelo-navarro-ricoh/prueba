package com.pelayo.integrations.sisnet.helpers
import com.pelayo.integrations.common.utils.CommonFunctions
import com.pelayo.integrations.sisnet.utils.SisnetConstants
import com.pelayo.integrations.common.utils.CommonFunctions

/*
 * Inicialización requerida para la ejecución de SISNet
 */
def sisnetInit(){
    def commonFunctions=new CommonFunctions()

    //Publicación del artefacto en nexus
    commonFunctions.artifactPublish(
        "${SisnetConstants.ENTORNOS_SISNET."${env.TARGET_ENVIRONMENT}".ARTIFACTREPOSITORY.URL}${SisnetConstants.ENTORNOS_SISNET."${env.TARGET_ENVIRONMENT}".ARTIFACTREPOSITORY.REPOSITORY}",
        SisnetConstants.ENTORNOS_SISNET."${env.TARGET_ENVIRONMENT}".ARTIFACTREPOSITORY.GROUPID, 
        SisnetConstants.artifacts.INTEGRACIONES, 
        "${env.ARTIFACT_VERSION}", 
        "./${env.workspaceSisnetRelativeDirArtifact}/IntegracionesPelayo-${env.ARTIFACT_VERSION}.jar", 
        "jar")
    commonFunctions.artifactPublish(
        "${SisnetConstants.ENTORNOS_SISNET."${env.TARGET_ENVIRONMENT}".ARTIFACTREPOSITORY.URL}${SisnetConstants.ENTORNOS_SISNET."${env.TARGET_ENVIRONMENT}".ARTIFACTREPOSITORY.REPOSITORY}",
        SisnetConstants.ENTORNOS_SISNET."${env.TARGET_ENVIRONMENT}".ARTIFACTREPOSITORY.GROUPID, 
        SisnetConstants.artifacts.SISNET, 
        "${env.ARTIFACT_VERSION}", 
        "./${env.workspaceSisnetRelativeDirArtifact}/${SisnetConstants.artifacts.SISNET}-${env.ARTIFACT_VERSION}.jar", 
        "jar")
    commonFunctions.artifactPublish(
        "${SisnetConstants.ENTORNOS_SISNET."${env.TARGET_ENVIRONMENT}".ARTIFACTREPOSITORY.URL}${SisnetConstants.ENTORNOS_SISNET."${env.TARGET_ENVIRONMENT}".ARTIFACTREPOSITORY.REPOSITORY}",
        SisnetConstants.ENTORNOS_SISNET."${env.TARGET_ENVIRONMENT}".ARTIFACTREPOSITORY.GROUPID, 
        SisnetConstants.artifacts.SISNETVAD, 
        "${env.ARTIFACT_VERSION}", 
        "./${env.workspaceSisnetRelativeDirArtifact}/${SisnetConstants.artifacts.SISNETVAD}-${env.ARTIFACT_VERSION}.jar", 
        "jar")
    // Si no es producción, no subimos el war.
    if (env.TARGET_ENVIRONMENT != "PROD") {
        println('SisnetPublishHelper: Como no es producción, no desplegamos el war en nexus.')
        return
    }
    commonFunctions.artifactPublish(
        "${SisnetConstants.ENTORNOS_SISNET."${env.TARGET_ENVIRONMENT}".ARTIFACTREPOSITORY.URL}${SisnetConstants.ENTORNOS_SISNET."${env.TARGET_ENVIRONMENT}".ARTIFACTREPOSITORY.REPOSITORY}",
        SisnetConstants.ENTORNOS_SISNET."${env.TARGET_ENVIRONMENT}".ARTIFACTREPOSITORY.GROUPID, 
        SisnetConstants.artifacts.SISNETWAR, 
        "${env.ARTIFACT_VERSION}", 
        "./${env.workspaceSisnetRelativeDirArtifact}/sisnet.war", 
        "war")
}

/**
* Enviar evidencia a la cmdb.
* @param jenkinsStatus estado del la tarea de jenkins a registrar
* @deployStatus estado de la aplicacion una vez desplegada
*/
def sendToCmdb (jenkinsStatus,deployStatus) {   

    // llamar a la CMDB
    new CommonFunctions().notifyJobCMDB(jenkinsStatus,deployStatus,env.ARTIFACT_VERSION,"publish");

  
}