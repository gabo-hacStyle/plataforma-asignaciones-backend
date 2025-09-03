# 🚀 Configuración Rápida de Emails

## **1. Configurar Gmail**

### **Paso 1: Habilitar Autenticación de 2 Factores**
1. Ve a tu cuenta de Google
2. Seguridad → Verificación en 2 pasos → Activar
3. Sigue los pasos para configurar

### **Paso 2: Generar Contraseña de Aplicación**
1. Seguridad → Contraseñas de aplicación
2. Selecciona "Correo" y "Windows"
3. Copia la contraseña generada (16 caracteres)

### **Paso 3: Configurar Variables de Entorno**
```bash
# En tu archivo .env o variables de entorno
MAIL_USERNAME=tu_email@gmail.com
MAIL_PASSWORD=tu_contraseña_de_aplicacion_16_caracteres
```

## **2. Probar Configuración**

### **Opción A: Usar Perfil de Desarrollo**
```bash
# Ejecutar con perfil dev
java -jar backend.jar --spring.profiles.active=dev
```

### **Opción B: Usar Variables de Entorno**
```bash
# Windows
set MAIL_USERNAME=tu_email@gmail.com
set MAIL_PASSWORD=tu_contraseña_de_aplicacion

# Linux/Mac
export MAIL_USERNAME=tu_email@gmail.com
export MAIL_PASSWORD=tu_contraseña_de_aplicacion
```

## **3. Verificar Funcionamiento**

### **Logs Esperados:**
```
✅ Email enviado exitosamente a: usuario@gmail.com
📧 Intentando enviar email a: usuario@gmail.com
```

### **Errores Comunes:**
- ❌ `SSLHandshakeException`: Verificar contraseña de aplicación
- ❌ `Authentication failed`: Credenciales incorrectas
- ❌ `Connection timeout`: Problema de red/firewall

## **4. Solución de Problemas**

### **Error SSL/TLS:**
```properties
# Agregar a application.properties si persiste el error
spring.mail.properties.mail.smtp.ssl.trust=*
spring.mail.properties.mail.smtp.ssl.protocols=TLSv1.2,TLSv1.3
```

### **Error de Autenticación:**
1. Verifica que uses contraseña de aplicación (16 caracteres)
2. No uses tu contraseña normal de Gmail
3. Asegúrate de que la autenticación de 2 factores esté activada

### **Error de Conexión:**
1. Verifica tu conexión a internet
2. Asegúrate de que el puerto 587 no esté bloqueado
3. Prueba con un firewall desactivado temporalmente

---

**¡Con estos pasos deberías tener emails funcionando! 🎉**




