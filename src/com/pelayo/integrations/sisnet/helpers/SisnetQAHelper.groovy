package com.pelayo.integrations.sisnet.helpers
import com.pelayo.integrations.common.utils.CommonFunctions
/*
 * Inicialización requerida para la ejecución de SISNet
 */
def sisnetInit(){
    println("""##################################
## Stage QA aún no implementado ##
##################################""")
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
