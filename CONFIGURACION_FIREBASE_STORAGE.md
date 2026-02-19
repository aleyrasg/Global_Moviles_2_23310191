# 🔥 CONFIGURACIÓN URGENTE DE FIREBASE STORAGE

## ⚠️ ERROR ACTUAL

```
StorageException: User does not have permission to access this object.
Code: -13021 HttpResult: 403
Caused by: java.io.IOException: Permission denied
```

**Tu app NO puede subir fotos** porque Firebase Storage no tiene reglas configuradas.

---

## 🚨 SOLUCIÓN INMEDIATA (5 minutos)

### **Paso 1: Abre Firebase Console**

1. **Abre tu navegador** (Chrome, Safari, etc.)
2. Ve a: https://console.firebase.google.com/
3. **Inicia sesión** con tu cuenta de Google
4. Selecciona tu proyecto: **"Global_Moviles_2_23310191"** o el nombre que le hayas puesto

---

### **Paso 2: Ve a Storage**

En el menú lateral izquierdo:
1. Busca el ícono de carpeta 📁 o busca **"Storage"**
2. Haz clic en **"Storage"**
3. Si te pide que actives Storage, haz clic en **"Comenzar"** o **"Get Started"**
4. Selecciona la ubicación (por ejemplo: "us-central1") y confirma

---

### **Paso 3: Configura las Reglas**

1. En la parte superior, verás pestañas: **"Files"**, **"Rules"**, etc.
2. Haz clic en la pestaña **"Rules"** (Reglas)
3. Verás un editor de texto con reglas existentes
4. **BORRA TODO** el contenido actual
5. **COPIA Y PEGA** exactamente estas reglas:


---

### **Paso 2: Configura las Reglas**

1. En la parte superior, verás pestañas: **"Files"**, **"Rules"**, etc.
2. Haz clic en la pestaña **"Rules"** (Reglas)
3. Verás un editor de texto con reglas existentes
4. **BORRA TODO** el contenido actual
5. **COPIA Y PEGA** exactamente estas reglas:

```javascript
rules_version = '2';
service firebase.storage {
  match /b/{bucket}/o {
    match /{allPaths=**} {
      allow read, write: if request.auth != null;
    }
  }
}
```

6. Haz clic en el botón **"Publish"** (Publicar) o **"Guardar"**
7. Espera a que aparezca un mensaje de confirmación (puede decir "Rules deployed successfully")

---

### **Paso 4: VERIFICA que las reglas estén activas**

1. Refresca la página de Rules
2. Deberías ver exactamente las 7 líneas que pegaste
3. Si ves algo diferente, repite el Paso 3

---

## ✅ PRUEBA LA APP

1. **Cierra completamente la app** en tu dispositivo/emulador
2. **Vuelve a abrir la app**
3. Ve a **"Progreso"**
4. Presiona **+**
5. Llena los datos y **selecciona una o más fotos**
6. Presiona **"Guardar"**

**Resultado esperado:** 
- ✅ La entrada se guarda
- ✅ Las fotos se suben correctamente
- ✅ NO aparece el error "Permission denied" en Logcat

---

## 🔍 SI AÚN NO FUNCIONA

### **Opción A: Reglas más permisivas (SOLO PARA DESARROLLO)**

Si las reglas anteriores no funcionan, prueba estas (MÁS PERMISIVAS):

```javascript
rules_version = '2';
service firebase.storage {
  match /b/{bucket}/o {
    match /{allPaths=**} {
      allow read, write: if true;
    }
  }
}
```

⚠️ **ADVERTENCIA:** Estas reglas permiten que CUALQUIERA lea/escriba archivos. 
**SOLO úsalas temporalmente** para verificar que funcione, luego cambia a las reglas seguras.

---

## 📱 VERIFICAR EN FIREBASE CONSOLE

Después de subir fotos exitosamente:

1. Ve a **Firebase Console > Storage > Files**
2. Deberías ver carpetas:
   - `progress/{tu-user-id}/archivo.jpg`
   - `places/{tu-user-id}/archivo.jpg`
3. Haz clic en una foto para ver que se subió correctamente

---

## 🔒 REGLAS SEGURAS (RECOMENDADAS)

Una vez que confirmes que funciona, cambia las reglas a estas (MÁS SEGURAS):

```javascript
rules_version = '2';
service firebase.storage {
  match /b/{bucket}/o {
    
    // Carpeta de lugares con fotos
    match /places/{userId}/{filename} {
      allow read: if request.auth != null;
      allow write: if request.auth != null 
                   && request.auth.uid == userId
                   && request.resource.size < 5 * 1024 * 1024
                   && request.resource.contentType.matches('image/.*');
    }
    
    // Carpeta de progreso con fotos
    match /progress/{userId}/{filename} {
      allow read: if request.auth != null;
      allow write: if request.auth != null 
                   && request.auth.uid == userId
                   && request.resource.size < 5 * 1024 * 1024
                   && request.resource.contentType.matches('image/.*');
    }
  }
}
```

**Estas reglas:**
- ✅ Solo usuarios autenticados pueden leer/escribir
- ✅ Cada usuario solo puede subir a SU propia carpeta
- ✅ Límite de 5 MB por imagen
- ✅ Solo permite archivos de tipo imagen

---

## 🆘 SOLUCIÓN ALTERNATIVA (Sin configurar Firebase)

Si **NO puedes acceder a Firebase Console** por alguna razón:

La app seguirá funcionando pero:
- ❌ Las fotos **NO** se subirán
- ✅ Los datos (medidas, fecha, hora) **SÍ** se guardarán
- ℹ️ No se mostrará el ícono de fotos en la lista

El código ya maneja este error sin crashear la app.

---

## 📞 SOPORTE

Si después de seguir todos los pasos el error persiste:

1. Verifica que estás usando la cuenta de Google correcta en Firebase
2. Confirma que el proyecto en Firebase Console es el mismo que en tu `google-services.json`
3. Asegúrate de hacer clic en "Publish" después de cambiar las reglas
4. Espera 1-2 minutos después de publicar las reglas

---

**Última actualización:** 2026-02-19  
**Estado:** ⚠️ REQUIERE CONFIGURACIÓN INMEDIATA

