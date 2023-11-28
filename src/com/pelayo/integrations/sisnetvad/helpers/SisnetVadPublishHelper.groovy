package com.pelayo.integrations.sisnetvad.helpers

import com.pelayo.integrations.common.utils.CommonFunctions
import com.pelayo.integrations.sisnetvad.utils.SisnetVadConstants

/*
 * Inicialización requerida para la ejecución de SISNet
 */
def sisnetVadInit(){
    // Si no es producción, no subimos el war.
    if (env.TARGET_ENVIRONMENT != "PROD") {
        println('SisnetVADPublishHelper: Como no es producción, no desplegamos el war en nexus.')
        return
    }
    def commonFunctions=new CommonFunctions()
    commonFunctions.artifactPublish(
        "${SisnetVadConstants.ENTORNOS_SISNETVAD."${env.TARGET_ENVIRONMENT}".ARTIFACTREPOSITORY.URL}${SisnetVadConstants.ENTORNOS_SISNETVAD."${env.TARGET_ENVIRONMENT}".ARTIFACTREPOSITORY.REPOSITORY}",
        SisnetVadConstants.ENTORNOS_SISNETVAD."${env.TARGET_ENVIRONMENT}".ARTIFACTREPOSITORY.GROUPID, 
        SisnetVadConstants.artifacts.SISNETVADWAR, 
        "${env.ARTIFACT_VERSION}", 
        "./${env.workspaceSisnetVadRelativeDirArtifact}/vad.war", 
        "war"
    )
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
