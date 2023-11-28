package com.pelayo.integrations.sisnetv6.utils

import com.pelayo.integrations.common.utils.CommonConstants

//Comprueba que la accion especificada está entre las acciones posibles
def isTypeValid(desiredStep) {
    // Se comprueba que el tipo esta entre los permitidos
    if(!(CommonConstants.desiredSteps.findAll{it.key == desiredStep}.size()>=1)){
        error("## ERROR: El tipo [${desiredStep}] no está entre los permitidos: ${CommonConstants.desiredSteps}")
    }
}

def obtainFileDeploymenByEnv(enviroment){
    def fileDeployment = "${env.SISNET_V6_CONFIG_FILES_DIRECTORY}/producto/${enviroment}/sisnet.xml";
    def existsFileDeployment = fileExists fileDeployment
    if (existsFileDeployment) {
        return fileDeployment
    } else {
        println "## WARNING: El fichero no existe en: ${fileDeployment}, Considere extraer la configuración a un repositorio propio de la aplicación!"
    }
}
