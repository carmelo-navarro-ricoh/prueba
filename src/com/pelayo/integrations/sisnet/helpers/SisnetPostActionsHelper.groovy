package com.pelayo.integrations.sisnet.helpers

import com.pelayo.integrations.common.utils.CommonFunctions
import com.pelayo.integrations.common.utils.xmlsql
import com.pelayo.integrations.sisnet.utils.SisnetFunctions
import com.pelayo.integrations.sisnet.utils.SisnetConstants


/*
 * Inicialización requerida para la ejecución de SISNet
 */
def sisnetInit(){
    println("""###########################################
## Stage PostActions aún no implementado ##
###########################################""")
}

/**
* Enviar evidencia a la cmdb.
* @param jenkinsStatus estado del la tarea de jenkins a registrar
* @deployStatus estado de la aplicacion una vez desplegada
*/
def sendToCmdb (jenkinsStatus,deployStatus) {   
    // llamar a la CMDB
    new CommonFunctions().notifyJobCMDB(jenkinsStatus,deployStatus,env.ARTIFACT_VERSION,"postactions");
}