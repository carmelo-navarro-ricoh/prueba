package com.pelayo.integrations.sisnetvad.helpers
import com.pelayo.integrations.common.utils.CommonFunctions
import com.pelayo.integrations.sisnetvad.utils.SisnetVadConstants

/*
 * Inicialización requerida para la ejecución del BUILD de SISNetVAD
 */
def sisnetVadInit(options){
    sh("cp ./${env.workspaceSisnetVadRelativeDirConfig}/${env.gitSisnetVadConfigMavenPathPom}/pom_assembly.xml ./${env.workspaceSisnetVadRelativeDirSrc}/pom_assembly.xml")
    dir("./${env.workspaceSisnetVadRelativeDirSrc}") {
        //Lectura del POM carrespondiente al entorno de SisnetVad
        def pom = readMavenPom file: "./pom_assembly.xml"
        pom.properties."jar.version"=env.ARTIFACT_VERSION
        pom.version=env.ARTIFACT_VERSION
        writeMavenPom model: pom, file: "./pom_assembly.xml"
        // Compilar SisnetVAD

        def mvnHome = tool 'maven 3.3.9'
	    def mvnExec = mvnHome + "/bin/mvn"
        sh("${mvnExec} --no-snapshot-updates -f ./pom_assembly.xml clean install")
        
    }

    sh("mkdir -p ./${env.workspaceSisnetVadRelativeDirArtifact}")
    //Mover el archivo sisnet.war al workspace y renombrarlo (Revisar)
    sh("cp ./${env.workspaceSisnetVadRelativeDirSrc}/target/vad.war ./${env.workspaceSisnetVadRelativeDirArtifact}/vad.war")
}

/**
* Enviar evidencia a la cmdb.
* @param jenkinsStatus estado del la tarea de jenkins a registrar
* @deployStatus estado de la aplicacion una vez desplegada
*/
def sendToCmdb (jenkinsStatus,deployStatus) {   

    // llamar a la CMDB
    new CommonFunctions().notifyJobCMDB(jenkinsStatus,deployStatus,env.ARTIFACT_VERSION,"build");

  
}

