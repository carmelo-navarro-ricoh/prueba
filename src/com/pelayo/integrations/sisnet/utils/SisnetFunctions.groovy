package com.pelayo.integrations.sisnet.utils


def sendInfoScriptBBDDToCMDB(app,environment,branch,file,type,hash,debug=false){
    //Verificar si se ha incluido contenido el la variable url
    if(app=="" || app==null){
        println("## ERROR: No se ha indicado una aplicación para su notificación a la CMDB")
        return false
    }
    //Verificar si se ha incluido el entorno
    switch(environment.toUpperCase()) {
        case ["DESA","INTG","ACPT","PREP","PROD"]:
        break
        default:
            println("## ERROR: No se ha indicado un entorno válido para su notificación en la CMDB")
            return false
        break
    }

    //Verificar si se ha incluido el nombre de un branch
    if(branch=="" || branch==null){
        println("## ERROR: No se ha indicado un branch para su notificación en la CMDB")
        return false
    }
    //Verificar si se ha incluido el nombre de un fichero
    if(file=="" || file==null){
        println("## ERROR: No se ha indicado un branch para su notificación en la CMDB")
        return false
    }
    //Verificar si se ha incluido el tipo de fichero válido
    switch(type.toUpperCase()) {
        case ["SQL","XML"]:
        break
        default:
            println("## ERROR: No se ha indicado un tipo de archivo válido para su notificación en la CMDB")
            return false
        break
    }
    //Verificar si se ha incluido el hash del fichero
    if(hash=="" || hash==null){
        println("## ERROR: No se ha indicado un hash para su notificación en la CMDB")
        return false
    }

    def responseCurl = sh(returnStdout: true, script: "curl -g -k -L -X POST ${url} -H \"Authorization: Basic ${credentialAuthorization}\" -H \"Content-Type: application/json\" -d \"{\\\"fichero\\\":\\\"${base64Text}\\\"}\" ").trim()
}