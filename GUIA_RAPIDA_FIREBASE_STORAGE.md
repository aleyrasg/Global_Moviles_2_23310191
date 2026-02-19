# 🔥 GUÍA RÁPIDA: ARREGLAR ERROR DE FOTOS

## ❌ PROBLEMA
```
StorageException: Permission denied (403)
```

## ✅ SOLUCIÓN (3 PASOS)

---

### 📍 **PASO 1: Abre Firebase Console**

1. Abre tu navegador
2. Ve a: **https://console.firebase.google.com/**
3. Inicia sesión con tu cuenta de Google
4. Selecciona tu proyecto

---

### 📍 **PASO 2: Ve a Storage Rules**

1. En el menú izquierdo, busca **"Storage"** 📁
2. Haz clic en **"Storage"**
3. Arriba verás pestañas: **Files | Rules | Usage**
4. Haz clic en **"Rules"**

---

### 📍 **PASO 3: Copia estas reglas**

1. **BORRA** todo lo que esté en el editor
2. **COPIA** y **PEGA** esto:

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

3. Haz clic en **"Publish"** (arriba a la derecha)
4. Espera el mensaje de confirmación

---

## ✅ LISTO

Ahora:
1. Cierra la app en tu teléfono/emulador
2. Vuelve a abrir la app
3. Intenta subir fotos de nuevo

**Debería funcionar sin el error de Permission denied.**

---

## ⚠️ SI NO TIENES ACCESO A FIREBASE

La app seguirá funcionando, pero:
- Los datos (medidas, fecha, hora) SÍ se guardarán ✅
- Las fotos NO se subirán ❌

---

## 📞 ¿SIGUES CON PROBLEMAS?

Lee el archivo completo: **CONFIGURACION_FIREBASE_STORAGE.md**

