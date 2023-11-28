package com.pelayo.integrations.sisnetv6.helpers

import com.pelayo.integrations.common.utils.CommonFunctions
import com.pelayo.integrations.common.utils.CommonConstants
import com.pelayo.integrations.common.utils.DependencyCheck

/*
 * Inicialización requerida para la ejecución de SISNet
 */
def sisnetV6Init(){
    
}

/**
* Enviar evidencia a la cmdb.
* @param jenkinsStatus estado del la tarea de jenkins a registrar
* @deployStatus estado de la aplicacion una vez desplegada
*/
def sendToCmdb (jenkinsStatus,deployStatus) {   
    // llamar a la CMDB
    new CommonFunctions().notifyJobCMDB(jenkinsStatus,deployStatus,env.ARTIFACT_VERSION,"qa");
}