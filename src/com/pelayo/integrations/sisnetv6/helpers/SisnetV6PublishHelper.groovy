package com.pelayo.integrations.sisnetv6.helpers

import com.pelayo.integrations.sisnetv6.utils.SisnetV6Constants
import com.pelayo.integrations.common.utils.CommonFunctions

def sisnetV6Init(){
    // Si no es producción, no subimos el war.
    if (env.TARGET_ENVIRONMENT != "PROD") {
        println('SisnetV6PublishHelper: Como no es producción, no desplegamos el war en nexus.')
        return
    }
    
    //Añadir Tags al repositorio
    new CommonFunctions().gitTag("${env.ARTIFACT_VERSION}")
    artifactPublish("${SisnetV6Constants.artifactRepository."${env.TARGET_ENVIRONMENT}".URL}${SisnetV6Constants.artifactRepository."${env.TARGET_ENVIRONMENT}".REPOSITORY}", 
        "${env.ARTIFACT_GROUPID}", 
        "${env.ARTIFACT_NAME}", 
        "${env.ARTIFACT_VERSION}", 
        "${SisnetV6Constants.NEW_WAR_GENERATED}", 
        "war")
}

//Publicar artefacto en el nexus
def artifactPublish(String nexus_url, String groupId, String artifactId, String version, String file, String filetype){
    def mvnHome = tool 'maven 3.6.0'
    println("""#######################################
## Publicación de artefacto en nexus ##
## Nexus URL: ${nexus_url}
## GroupID: ${groupId}
## Artefacto: ${artifactId}
## Versión: ${version}
## Tipo de fichero: ${filetype}
## Fichero: ${file}
## Comando:
${mvnHome} -B deploy:deploy-file -DgroupId=${groupId} -DartifactId=${artifactId} -Dversion=${version} -Dpackaging=${filetype} -DuniqueVersion=false -Dfile=${file} -Durl=$nexus_url
#######################################""")

    configFileProvider([configFile(fileId: env.ID_CUSTOM_MAVEN_SETTINGS, variable: 'MAVEN_SETTINGS_XML')]) {
        //println("sh(\"${mvnHome} -B deploy:deploy-file -DgroupId=${groupId} -DartifactId=${artifactId} -Dversion=${version} -Dpackaging=${filetype} -DuniqueVersion=false -Dfile=${file} -Durl=$nexus_url -gs ${MAVEN_SETTINGS_XML}\")")
        sh("${mvnHome} -B deploy:deploy-file -DgroupId=${groupId} -DartifactId=${artifactId} -Dversion=${version} -Dpackaging=${filetype} -DuniqueVersion=false -Dfile=${file} -Durl=$nexus_url -gs ${MAVEN_SETTINGS_XML}")
    }
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