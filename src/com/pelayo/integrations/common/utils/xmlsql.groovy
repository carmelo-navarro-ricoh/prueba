package com.pelayo.integrations.common.utils

//http://192.6.4.109:8081/sisnet/api/alm/1/importar/complejo
/*curl -L -X POST "http://192.6.4.109:8081/sisnet/api/alm/1/importar/complejo" -H "Authorization: Basic QVJRVUlURUNUVVJBOnBydWViYXMy" -H "Content-Type: application/json" --data-raw "{
    \"fichero\":\"PD94bWwgdmVyc2lvbj0iMS4wIiBlbmNvZGluZz0iSVNPLTg4NTktMTUiID8+CjxST09UPgo8VElQT19DT01QTEVKTz5SVUxFUzwvVElQT19DT01QTEVKTz4KPERBVE9fQ09NUExFSk8+CjxUQUJMQT5EUlVMVVNFUjwvVEFCTEE+CjxPUEVSQUNJT04+QUxUQTwvT1BFUkFDSU9OPgo8VFlQRT5TT1VSQ0U8L1RZUEU+CjxLRVlTPgo8S0VZIE5PTUJSRT0iVFlQRSI+U09VUkNFPC9LRVk+CjxLRVkgTk9NQlJFPSJSVUxFU0NPRCI+UFJVRUJBU0FSUTwvS0VZPgo8L0tFWVM+CjxEQVRPUz4KPERBVE8gTk9NQlJFPSJOVU1TSVRVQSI+MjwvREFUTz4KPC9EQVRPUz4KPENPTVBMRUpPUz4KPENPTVBMRUpPIEtFWT0iSkFWQSI+cGFja2FnZSByZWdsYXM7DQoNCmltcG9ydCBmcmFtZXdvcmsuYmFzaWNvLkZlY2hhOwppbXBvcnQgZnJhbWV3b3JrLnJlZ2xhcy5tb3RvcmVzLklEYXRhT3BlcmF0aW9uOwppbXBvcnQgbW90b3Iuc2NyaXB0LlNjcmlwdEV2YWx1YWRvckRTdXNjcmlwY2lvbjsKaW1wb3J0IGZyYW1ld29yay5yZWdsYXMuUmVnbGFETmVnb2NpbzsKaW1wb3J0IGZyYW1ld29yay5leGNlcGNpb25lcy5FcnJvckdlbmVyYWw7CmltcG9ydCBmcmFtZXdvcmsucmVnbGFzLlJlc3B1ZXN0YUV2YWx1YWNpb247Cg0KcHVibGljIGZpbmFsIGNsYXNzIFBSVUVCQVNBUlFfMiBleHRlbmRzIFNjcmlwdEV2YWx1YWRvckRTdXNjcmlwY2lvbnsNCg0KcHVibGljIFBSVUVCQVNBUlFfMihSZWdsYUROZWdvY2lvIHJlZ2xhRE5lZ29jaW8gKXsNCnN1cGVyKHJlZ2xhRE5lZ29jaW8gKTsNCn0NCnB1YmxpYyB2b2lkIGluaXRSdWxlKEZlY2hhIGZlY2hhICkgdGhyb3dzIEVycm9yR2VuZXJhbCB7DQoNCn0NCnB1YmxpYyB2b2lkIGV2YWwoUmVzcHVlc3RhRXZhbHVhY2lvbiByZXNwdWVzdGFFdmFsdWFjaW9uLCBJRGF0YU9wZXJhdGlvbiBpRGF0YU9wZXJhdGlvbjEgKSB0aHJvd3MgRXJyb3JHZW5lcmFsIHsNClN5c3RlbS5vdXQucHJpbnQoJiMzNDtQcnVlYmFzIEFycXVpdGVjdHVyYSYjMzQ7KTsKU3lzdGVtLmV4aXQoMCk7DQp9DQoNCn08L0NPTVBMRUpPPgo8Q09NUExFSk8gS0VZPSJTT1VSQ0UiPiYjNjA7P3htbCB2ZXJzaW9uPSYjMzQ7MS4wJiMzNDsgZW5jb2Rpbmc9JiMzNDtJU08tODg1OS0xNSYjMzQ7ID8mIzYyOwomIzYwO01FVE9ET1MmIzYyOwomIzYwO0xFTkdVQUpFJiM2MjtTSVNTQ1ImIzYwOy9MRU5HVUFKRSYjNjI7CiYjNjA7TUVUT0RPJiM2MjsKJiM2MDtOT01CUkUmIzYyO2luaXRSdWxlJiM2MDsvTk9NQlJFJiM2MjsKJiM2MDtDT05URU5JRE8mIzYyOwomIzYwOyFbQ0RBVEFbCgpdXSYjNjI7JiM2MDsvQ09OVEVOSURPJiM2MjsKJiM2MDtDT1JSTElORUFTJiM2MjsKJiM2MDtDT1JSTElORUEmIzYyOwomIzYwO0xJTkVBSkFWQSYjNjI7MTYmIzYwOy9MSU5FQUpBVkEmIzYyOwomIzYwO0xJTkVBT1JJRyYjNjI7MSYjNjA7L0xJTkVBT1JJRyYjNjI7CiYjNjA7L0NPUlJMSU5FQSYjNjI7CiYjNjA7L0NPUlJMSU5FQVMmIzYyOwomIzYwOy9NRVRPRE8mIzYyOwomIzYwO01FVE9ETyYjNjI7CiYjNjA7Tk9NQlJFJiM2MjtldmFsJiM2MDsvTk9NQlJFJiM2MjsKJiM2MDtDT05URU5JRE8mIzYyOwomIzYwOyFbQ0RBVEFbClN5c3RlbS5vdXQucHJpbnQoJiMzNDtQcnVlYmFzIEFycXVpdGVjdHVyYSYjMzQ7KTsNClN5c3RlbS5leGl0KDApOwpdXSYjNjI7JiM2MDsvQ09OVEVOSURPJiM2MjsKJiM2MDtDT1JSTElORUFTJiM2MjsKJiM2MDtDT1JSTElORUEmIzYyOwomIzYwO0xJTkVBSkFWQSYjNjI7MTkmIzYwOy9MSU5FQUpBVkEmIzYyOwomIzYwO0xJTkVBT1JJRyYjNjI7MSYjNjA7L0xJTkVBT1JJRyYjNjI7CiYjNjA7L0NPUlJMSU5FQSYjNjI7CiYjNjA7Q09SUkxJTkVBJiM2MjsKJiM2MDtMSU5FQUpBVkEmIzYyOzIwJiM2MDsvTElORUFKQVZBJiM2MjsKJiM2MDtMSU5FQU9SSUcmIzYyOzImIzYwOy9MSU5FQU9SSUcmIzYyOwomIzYwOy9DT1JSTElORUEmIzYyOwomIzYwOy9DT1JSTElORUFTJiM2MjsKJiM2MDsvTUVUT0RPJiM2MjsKJiM2MDsvTUVUT0RPUyYjNjI7PC9DT01QTEVKTz4KPENPTVBMRUpPIEtFWT0iQllURUNPREUiPnsKICAmIzM0O3JlZ2xhcy5QUlVFQkFTQVJRXzImIzM0OzogJiMzNDt5djY2dmdBQUFESUFNUW9BQndBZkNRQWdBQ0VJQUNJS0FDTUFKQW9BSUFBbEJ3QW1Cd0FuQVFBR1BHbHVhWFErQVFBaktFeG1jbUZ0WlhkdmNtc3ZjbVZuYkdGekwxSmxaMnhoUkU1bFoyOWphVzg3S1ZZQkFBUkRiMlJsQVFBUFRHbHVaVTUxYldKbGNsUmhZbXhsQVFBU1RHOWpZV3hXWVhKcFlXSnNaVlJoWW14bEFRQUVkR2hwY3dFQUZVeHlaV2RzWVhNdlVGSlZSVUpCVTBGU1VWOHlPd0VBRFhKbFoyeGhSRTVsWjI5amFXOEJBQ0JNWm5KaGJXVjNiM0pyTDNKbFoyeGhjeTlTWldkc1lVUk9aV2R2WTJsdk93RUFDR2x1YVhSU2RXeGxBUUFiS0V4bWNtRnRaWGR2Y21zdlltRnphV052TDBabFkyaGhPeWxXQVFBRlptVmphR0VCQUJoTVpuSmhiV1YzYjNKckwySmhjMmxqYnk5R1pXTm9ZVHNCQUFwRmVHTmxjSFJwYjI1ekJ3QW9BUUFFWlhaaGJBRUFVaWhNWm5KaGJXVjNiM0pyTDNKbFoyeGhjeTlTWlhOd2RXVnpkR0ZGZG1Gc2RXRmphVzl1TzB4bWNtRnRaWGR2Y21zdmNtVm5iR0Z6TDIxdmRHOXlaWE12U1VSaGRHRlBjR1Z5WVhScGIyNDdLVllCQUJOeVpYTndkV1Z6ZEdGRmRtRnNkV0ZqYVc5dUFRQW1UR1p5WVcxbGQyOXlheTl5Wldkc1lYTXZVbVZ6Y0hWbGMzUmhSWFpoYkhWaFkybHZianNCQUE5cFJHRjBZVTl3WlhKaGRHbHZiakVCQUNsTVpuSmhiV1YzYjNKckwzSmxaMnhoY3k5dGIzUnZjbVZ6TDBsRVlYUmhUM0JsY21GMGFXOXVPd0VBQ2xOdmRYSmpaVVpwYkdVQkFCRlFVbFZGUWtGVFFWSlJYekl1YW1GMllRd0FDQUFKQndBcERBQXFBQ3NCQUJSUWNuVmxZbUZ6SUVGeWNYVnBkR1ZqZEhWeVlRY0FMQXdBTFFBdURBQXZBREFCQUJOeVpXZHNZWE12VUZKVlJVSkJVMEZTVVY4eUFRQW9iVzkwYjNJdmMyTnlhWEIwTDFOamNtbHdkRVYyWVd4MVlXUnZja1JUZFhOamNtbHdZMmx2YmdFQUltWnlZVzFsZDI5eWF5OWxlR05sY0dOcGIyNWxjeTlGY25KdmNrZGxibVZ5WVd3QkFCQnFZWFpoTDJ4aGJtY3ZVM2x6ZEdWdEFRQURiM1YwQVFBVlRHcGhkbUV2YVc4dlVISnBiblJUZEhKbFlXMDdBUUFUYW1GMllTOXBieTlRY21sdWRGTjBjbVZoYlFFQUJYQnlhVzUwQVFBVktFeHFZWFpoTDJ4aGJtY3ZVM1J5YVc1bk95bFdBUUFFWlhocGRBRUFCQ2hKS1ZZQU1RQUdBQWNBQUFBQUFBTUFBUUFJQUFrQUFRQUtBQUFBUGdBQ0FBSUFBQUFHS2l1M0FBR3hBQUFBQWdBTEFBQUFDZ0FDQUFBQURRQUZBQTRBREFBQUFCWUFBZ0FBQUFZQURRQU9BQUFBQUFBR0FBOEFFQUFCQUFFQUVRQVNBQUlBQ2dBQUFEVUFBQUFDQUFBQUFiRUFBQUFDQUFzQUFBQUdBQUVBQUFBUkFBd0FBQUFXQUFJQUFBQUJBQTBBRGdBQUFBQUFBUUFUQUJRQUFRQVZBQUFBQkFBQkFCWUFBUUFYQUJnQUFnQUtBQUFBVXdBQ0FBTUFBQUFOc2dBQ0VnTzJBQVFEdUFBRnNRQUFBQUlBQ3dBQUFBNEFBd0FBQUJNQUNBQVVBQXdBRlFBTUFBQUFJQUFEQUFBQURRQU5BQTRBQUFBQUFBMEFHUUFhQUFFQUFBQU5BQnNBSEFBQ0FCVUFBQUFFQUFFQUZnQUJBQjBBQUFBQ0FCNFx1MDAzZCYjMzQ7Cn08L0NPTVBMRUpPPgo8L0NPTVBMRUpPUz4KCjwvREFUT19DT01QTEVKTz4KPC9ST09UPgo=\"
}"*/
def uploadXML(url,file,environment,debug=true){
    println("## INFO: Se procede a la ejecución del XML Complejo")
    def response=true
    //Verificar si se ha incluido una pesaña
    if(url=="" || url==null){
        println("## WARNING: No se ha indicado una url válida para la ejecución del XML")
        return false
    }
    //Verificar si se ha incluido el nombre de un fichero XML
    if(file=="" || file==null){
        println("## WARNING: No se ha indicado un fichero para la ejecución del XML")
        return false
    }
    //Validación de los distintos entornos válidos para ejecutar los XML Complejos
    def credentialName = ""
    switch(environment.toUpperCase()) {
        case ["DESA","INTG"]:
            credentialName="jenkins_to_sisnet_api_desa"
        break
        case ["ACPT"]:
            credentialName="jenkins_to_sisnet_api_acpt"
        break
        case ["PREP"]:
            credentialName="jenkins_to_sisnet_api_prep"
        break
        case ["PROD"]:
            credentialName="jenkins_to_sisnet_api_prod"
        break
        default:
            println("## WARNING: No se ha indicado un entorno válido para la ejecución del XML")
            return false
        break
    }

    //Lectura del archivo XML    
    def fileContent= readFile "${file}"
    //Codificación del archivo XML en Base64
    def base64Text = "$fileContent".bytes.encodeBase64().toString()
    def credentialAuthorization=""
    withCredentials([usernamePassword(credentialsId: credentialName, usernameVariable: 'USERNAME', passwordVariable: 'PASSWORD')]){
        credentialAuthorization="${USERNAME}:${PASSWORD}".bytes.encodeBase64().toString()
    }
    //Envio de petición a la api de Sisnet
    def responseCurl = sh(returnStdout: true, script: "curl -g -k -L -X POST ${url} -H \"Authorization: Basic ${credentialAuthorization}\" -H \"Content-Type: application/json\" -d \"{\\\"fichero\\\":\\\"${base64Text}\\\"}\" ").trim()
    def responseData = readJSON text: responseCurl
    switch(responseData.header.state.toInteger()) {
        case 200..299:
            if(debug) {
                println("## INFO: Se ha ejecutado correctamente XML del fichero ${file}")
                }
        break
        case 300..599:
            println("## ERROR: No se ha podido ejecutar correctamente el XML ${file}\nCódigo respuesta: ${responseData.header.state}\n##        ${responseData.header.error.message}")
            response=false
        break
        default:
            println("## ERROR: Respuesta indeterminada al ejecutar el XML ${file}\n ##        ${responseData.header.error.message}")
            response=false
        break
    }
    return response
}

//Ordenamos los archivos de mantenimiento de la BBDD en carpetas
def orderDBFiles(fileList,fileSourcePath,filePath="."){
    //Separación en carpetas de los archivos
    def dataReturn=[]
    def count=0
    def filePrefix="scripts_"
    def fileType=""
    def filePathScripts=""
    fileList.each{
        def dataTemp=[:]
        if(fileType!=it.TYPE){
            count++
        }
        filePathScripts="${filePrefix}${count}"

        switch(it.TYPE){
            case ["XML"]:
                dataTemp.TYPE=it.TYPE
                dataTemp.PATH=filePathScripts
                dataTemp.FILE=it.FILE
            break
            case ["SQL"]:
                if(fileType!=it.TYPE){
                    dataTemp.TYPE=it.TYPE
                    dataTemp.PATH=filePathScripts
                    dataTemp.SCHEMA=it.SCHEMA
                }
            break
        }

        dir("${filePath}/${filePathScripts}"){
            sh("cp ${WORKSPACE}/${fileSourcePath}/${it.FILE} .")

        fileType=it.TYPE

        dataReturn.add(dataTemp)
        }
    }
    return dataReturn
}

def cmdbFilesSearch(app,env,branch,file,type,debug=true){
    def conditionUrl=""
    if(app!="" && app!=null){
        if(conditionUrl!="")
            conditionUrl+="&"    
        conditionUrl+="app=${app}"
    }
    if(env!="" && env!=null){
        if(conditionUrl!="")
            conditionUrl+="&"    
        conditionUrl+="env=${env}"
    }
    if(branch!="" && branch!=null){
        if(conditionUrl!="")
            conditionUrl+="&"    
        conditionUrl+="branch=${branch}"
    }
    if(file!="" && file!=null){
        if(conditionUrl!="")
            conditionUrl+="&"    
        conditionUrl+="file=${file}"
    }
    if(type!="" && type!=null){
        if(conditionUrl!="")
            conditionUrl+="&"    
        conditionUrl+="type=${type}"
    }
    if(conditionUrl!="")
        conditionUrl+="&"
    conditionUrl+="unPaged=true&page=0"

    def cmdbFilesResult = sh(returnStdout: true, script: "curl -g -k -X GET 'http://integ-devops-dashboard-cmdb-application-srv-v1-rc.acpt-integ.istio-ingressgateway-istio-system.apps.arod02.pelayo.com/cmdb-file?${conditionUrl}' -H 'accept: application/json'").trim()
    def cmdbFilesOutput = readJSON text: cmdbFilesResult;

    return cmdbFilesOutput
}

def cmdbFilesUpload(app,env,branch,file,type,hash,res,debug=true){
    def ret=true
    if(app=="" || app==null){
        return false
    }
    if(env=="" || env==null){
        return false
    }
    if(branch=="" || branch==null){
        return false
    }
    if(file=="" || file==null){
        return false
    }
    if(type=="" || type==null){
        return false
    }
    if(hash=="" || hash==null){
        return false
    }
    if(res=="" || res==null){
        return false
    }

    def jsonCurl="""{
  "app": "${app}",
  "env": "${env}",
  "branch": "${branch}",
  "file": "${file}",
  "type": "${type}",
  "hash": "${hash}",
  "result": "${res}"
}"""

    def cmdbFilesResult = sh(returnStdout: true, script: "curl -g -k -X POST 'http://integ-devops-dashboard-cmdb-application-srv-v1-rc.acpt-integ.istio-ingressgateway-istio-system.apps.arod02.pelayo.com/cmdb-file' -H 'accept: application/json'   -H 'accept: application/json' -H 'Content-Type: application/json' -d '${jsonCurl}'").trim()
    def cmdbFilesOutput = readJSON text: cmdbFilesResult;

    return cmdbFilesOutput
}
/*
curl -X 'POST' \
  'http://integ-devops-dashboard-cmdb-application-srv-v1-rc.acpt-integ.istio-ingressgateway-istio-system.apps.arod02.pelayo.com/cmdb-file' \
  -H 'accept: application/json' \
  -H 'Content-Type: application/json' \
  -d '{
  "app": "SISNET",
  "env": "INTG",
  "branch": "TEST",
  "file": "TEST.XML",
  "type": "XML",
  "hash": "1234567890123456792"
}'
*/