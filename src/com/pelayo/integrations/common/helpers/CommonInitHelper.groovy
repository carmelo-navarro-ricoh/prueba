package com.pelayo.integrations.common.helpers
import com.pelayo.integrations.common.utils.CommonConstants
import com.pelayo.integrations.common.utils.CommonFunctions

/**
* Enviar evidencia a la cmdb.
* @param jenkinsStatus estado del la tarea de jenkins a registrar
* @deployStatus estado de la aplicacion una vez desplegada
*/
def sendToCmdb (jenkinsStatus,deployStatus) {   
    // llamar a la CMDB
    new CommonFunctions().notifyJobCMDB(jenkinsStatus,deployStatus,env.ARTIFACT_VERSION,"init");
}

/**
* Inicializar las variables globales necesarias para el envio a la CMDB
*/
def initCMDB(){
    // UUID para registrar el jobs en la cmdb
    env.JENKINS_JOB_CMDB_ID = UUID.randomUUID();  
    env.JENKINS_JOB_CMDB_OPERATION = "POST";  
    env.JENKINS_JOB_CMDB_URL = "";  
    env.JENKINS_JOB_CMDB_PATH_URL = "";
    env.JENKINS_JOB_CMDB_TIME = new Date().format("yyyy-MM-dd HH:mm:ss.SSS");  
    env.JENKINS_LAST_JOB_CMDB_TIME = env.JENKINS_JOB_CMDB_TIME;
    env.CMDB_APP_ID = "";
    env.CMDB_CURRENT_STATUS = "W";
    // en que entorno estamos probando, TODO esto se debe ver como se calcula
    //env.CMDB_ENV = CommonConstants.CMDB_ENV_DEV;
    env.CMDB_ENV = CommonConstants.CMDB_ENV_ACP;
}

def initWorkingMode(scmVars){
        // Detección si es una ejecución por webhook o por lanzamiento directo del job
    if (env.TAG_TO_BUILD) { //Proceso ejecutado  directamente con un job
        env.WORKING_MODE = CommonConstants.workingModesEnum.MANUAL_TAG.NAME
        env.TAG = env.TAG_TO_BUILD
        env.BRANCH_NAME=''
    } else {    //Proceso ejecutado por un webhook
        env.WORKING_MODE = CommonConstants.workingModesEnum.WEBHOOK.NAME
        if(scmVars.GIT_BRANCH.substring(0,6)=="origin"){
            env.BRANCH = "${scmVars.GIT_BRANCH}"
        } else {
            env.BRANCH = "origin/${scmVars.GIT_BRANCH}"
        }
        env.TAG = ''
    }
}