package com.pelayo.integrations.common.utils

//Ejecución del Dependency check de OWASP
def dependencyCheckExecute(srcPath=".",mavenVersion="3.3.9",owaspVersion="7.1.1",outputFormat="html,json"){
    def ret=true
    def startDate=new Date()
    configFileProvider([configFile(fileId: env.ID_CUSTOM_MAVEN_SETTINGS, variable: 'MAVEN_SETTINGS_XML')]) {
        try {
            sh("mvn org.owasp:dependency-check-maven:${owaspVersion}:check -Dformats=${outputFormat} -gs ${MAVEN_SETTINGS_XML}")
        } catch (Exception ex) {
            println("## WARNING: No se ha podido ejecutar correctamente el dependency-check:\n ${ex}")
            ret=false
        }

    }
    def finishDate=new Date()
    def duration=finishDate.getTime()-startDate.getTime()
    env.duration=Math.round(duration/1000)
    return ret
}

def dependencyCheckUploadResult(path="./target", fileNames=["dependency-check-report.html"]){
    fileNames.each{ fileName -> 
        println("INFO: Subida de archivo \"${fileName}\" al repositorio documental ----------------")
    }
}