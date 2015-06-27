# AWSEchoHack

This project is building an app for the Amazon Echo device: just compile it with maven `mvn package` then launch the app with the command `java -jar jetty-generic-ws.jar` in folder target.
To have this server working correctly, you will have to provide a certificate for it.

### Building certificate for the server

Generate a private key:
```
openssl genrsa -out private-key.pem 2048
```
Write a server.cnf file, with updated content:
```
[req]
distinguished_name = req_distinguished_name
x509_extensions = v3_req
prompt = no
 
[req_distinguished_name]
C = US
ST = MA
L = TEST
O = TEST
CN = TEST
 
[v3_req]
keyUsage = keyEncipherment, dataEncipherment
extendedKeyUsage = serverAuth
subjectAltName = @subject_alternate_names
 
[subject_alternate_names]
DNS.1 = {enter here your dns name}
```
Create the certificate:
```
openssl req -new -x509 -days 365 -key private-key.pem -config server.cnf -out certificate.pem
```
Prepare a key entry for the keystore:
```
openssl pkcs12 -keypbe PBE-SHA1-3DES -certpbe PBE-SHA1-3DES -inkey private-key.pem -in certificate.pem -export            -out keystore.pkcs12
```
Create a keystore:
```
keytool -genkey -alias jetty -keyalg RSA -keystore server.jks
```
Add your key entry to the newly created keystore:
```
keytool -importkeystore -destkeystore server.jks -srckeystore keystore.pkcs12 -srcstoretype PKCS12
```

Once terminated, you only have to provide the certificate.pem content to the AWS server.
Don't forget to update the server configuration password.


