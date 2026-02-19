# 🧪 GUÍA DE TESTING - VALIDACIÓN DE FUNCIONALIDADES

## 📱 ANTES DE EMPEZAR

1. **Sincroniza el proyecto:**
   ```
   File → Sync Now
   ```

2. **Compila el proyecto:**
   ```
   Build → Rebuild Project
   ```

3. **Ejecuta en emulador o dispositivo:**
   ```
   Run → Run 'app'
   ```

4. **Asume que:**
   - Ya tienes lugares creados en la app
   - Tienes permisos de ubicación habilitados
   - Internet funcionando

---

## ✅ TEST 1: CARGAR LUGARES EN EL MAPA

### Pasos:
1. Abre la app
2. Ve al menú (icono hamburguesa)
3. Toca "Mapa"
4. Espera a que cargue (verás indicator de carga si hay muchos lugares)

### Resultado esperado:
- ✅ Ves marcadores azules en el mapa
- ✅ Cada marcador corresponde a un lugar que creaste
- ✅ El mapa se centra automáticamente en tu ubicación (o Guadalajara si no hay permiso)

### Si FALLA:
```
Problema: No veo marcadores
Solución: 
1. Verifica que hayas creado lugares en "LUGARES"
2. Asegúrate que cada lugar tenga lat/lng (edita y usa mapa picker)
3. Revisa Logcat (View → Tool Windows → Logcat)
```

---

## ✅ TEST 2: VER INFO WINDOW

### Pasos:
1. Ya en el mapa (TEST 1 completado)
2. **Toca un marcador** (cualquier punto azul)
3. Espera a que aparezca el popup

### Resultado esperado:
```
┌─────────────────────────────┐
│ Fitness Club                │
│                             │
│ Calle Principal 123         │
│ Distancia: 2.3 km           │
│                             │
│ [Ver detalles]  [Cerrar]    │
└─────────────────────────────┘
```

- ✅ Aparece popup en parte inferior
- ✅ Muestra nombre del lugar
- ✅ Muestra dirección
- ✅ **Muestra distancia en km desde tu posición**
- ✅ Botones funcionales

### Si FALLA:

```
Problema: No aparece popup al tocar marcador
Solución:
1. Toca el CENTRO del marcador (no los bordes)
2. Si aún no funciona, revisa que lugares tengan lat/lng
3. Verifica logcat: adb logcat | grep -i marker
```

---

## ✅ TEST 3: DISTANCIA EN KM

### Pasos:
1. Ya en el mapa con popup abierto (TEST 2 completado)
2. **Lee el texto "Distancia: X km"**

### Resultado esperado:
```
Distancia: 2.3 km      ✅ Formato correcto
Distancia: 0.5 km      ✅ Distancias cortas
Distancia: 15.8 km     ✅ Distancias largas
```

### Validación:
- ✅ El número tiene 1-2 decimales
- ✅ Dice "km"
- ✅ El número es razonable (no está a 0.0 km si estás lejos)

### Si FALLA:

```
Problema: Distancia muestra 0.0 km o número raro
Solución:
1. Verifica permisos de ubicación:
   Settings → App → Global_Moviles → Permissions → Location
2. Asegúrate que el dispositivo/emulador tenga GPS/mock location
3. En emulador: Extended controls → Location → Manualmente setear coordenadas
```

---

## ✅ TEST 4: CREAR LUGAR DESDE MAPA

### Pasos:
1. En el mapa, busca el **botón flotante "+" en esquina inferior derecha**
2. **Presiona el botón "+"**
3. Se abrirá el formulario de crear lugar

### Resultado esperado:
```
Nuevo Lugar

Nombre:        [           ]
Dirección:     [           ]

Elegir ubicación en mapa (botón)

📍 20.6736, -103.344          <- Coordenadas PRE-RELLENAS
```

- ✅ Se abre formulario de crear lugar
- ✅ **Las coordenadas (lat/lng) están PRE-RELLENAS**
- ✅ Las coords son cercanas a tu ubicación actual

### Si FALLA:

```
Problema: Coordenadas no están pre-rellenas (dice 20.6736, -103.344)
Posible causa:
1. No tienes permisos de ubicación
2. Emulador sin GPS simulado

Solución:
1. Habilita permisos de ubicación
2. En emulador: Extended controls → Location → setea coordenadas
3. El formulario funciona igual, puedes editar las coords manualmente
```

### Continuando TEST 4:
4. **Llena el formulario:**
   - Nombre: "Mi Gym Nuevo"
   - Dirección: "Calle Test 123"
   
5. **Presiona "Guardar"**

6. **Vuelve al mapa** (debería hacerlo automáticamente)

### Resultado esperado (final):
- ✅ Ves un **nuevo marcador en el mapa** en la ubicación donde estabas
- ✅ El marcador aparece inmediatamente
- ✅ Si tocas el nuevo marcador, ves el nombre "Mi Gym Nuevo"

### Si FALLA:

```
Problema: Nuevo lugar no aparece en mapa
Solución:
1. Vuelve a HOME y regresa a MAPA (fuerza recarga)
2. Revisa que se guardó: ve a LUGARES, ¿está ahí?
3. Si está en LUGARES pero no en MAPA → verifica que tenga lat/lng
4. Revisa logcat
```

---

## ✅ TEST 5: BOTÓN "VER DETALLES"

### Pasos:
1. En el mapa, toca un marcador (TEST 2)
2. Aparece popup
3. **Presiona botón "Ver detalles"**

### Resultado esperado:
```
Se cierra el mapa y se abre:
┌───────────────────────────────┐
│ Editar Lugar                  │
│                               │
│ Nombre:        Fitness Club   │
│ Dirección:     Calle X 123    │
│ [Cambiar mapa ubicación]      │
│ 📍 20.6736, -103.344          │
│                               │
│ Descripción:   [           ]  │
│                               │
│ [Elegir foto] [Actualizar]    │
└───────────────────────────────┘
```

- ✅ Se abre pantalla de editar lugar
- ✅ Todos los campos están llenados
- ✅ Puedes editar cualquier cosa

---

## ✅ TEST 6: BOTÓN "CERRAR" (Info Window)

### Pasos:
1. En el mapa, toca un marcador
2. Aparece popup
3. **Presiona botón "Cerrar"**

### Resultado esperado:
- ✅ Popup desaparece
- ✅ Sigues en el mapa
- ✅ Puedes tocar otro marcador

---

## 🔄 TEST COMPLETO (Flujo Real)

### Escenario: Usuario quiere entrenar en un lugar cercano

1. **Abre app**
   ```
   HOME → Menú → MAPA
   ```

2. **Ve lugares cercanos**
   ```
   Toca un marcador → Lee: "Distancia: 2.3 km"
   → Decide: "Sí, voy"
   ```

3. **Ver detalles**
   ```
   Presiona "Ver detalles" → Ve fotos, dirección exacta
   ```

4. **Vuelve al mapa**
   ```
   Presiona atrás → Mapa de nuevo
   ```

5. **Encuentra un lugar nuevo**
   ```
   En el mapa, presiona "+" → Crea nuevo lugar
   Con coords ya llenadas → Solo agrega nombre
   → Listo
   ```

---

## 📊 TABLA DE TESTS

| # | Test | Pasos | Esperado | Estado |
|---|------|-------|----------|--------|
| 1 | Cargar lugares | Abre MAPA | Ve marcadores | [ ] |
| 2 | Info window | Toca marcador | Popup aparece | [ ] |
| 3 | Distancia | Lee popup | "X.X km" | [ ] |
| 4 | Crear lugar | Presiona "+" | Pre-rellena coords | [ ] |
| 4b | Crear lugar | Guarda | Aparece en mapa | [ ] |
| 5 | Ver detalles | Botón popup | Abre formulario | [ ] |
| 6 | Cerrar | Botón popup | Popup cierra | [ ] |

---

## 🐛 DEBUGGING

### Comando para ver logs:
```bash
adb logcat | grep -i "MapScreen\|PlaceViewModel\|DistanceUtils"
```

### Checar ubicación en emulador:
```bash
# Ver ubicación simulada actual
adb shell settings get secure mock_location

# Simular ubicación
adb emu geo fix 20.6736 -103.344
```

### Limpiar datos:
```bash
adb shell pm clear com.example.global_moviles_2_23310191
```

---

## ✨ BONUS: TESTS ADICIONALES

### Test: Lugares sin lat/lng
```
1. Ve a LUGARES
2. Edita un lugar antiguo
3. NO toques mapa picker
4. Guarda
5. En MAPA → NO debería aparecer ese lugar
   (Porque if (place.lat != null && place.lng != null))
```

### Test: Performance (muchos lugares)
```
1. Crea 50+ lugares en LUGARES
2. Abre MAPA
3. ¿Se carga rápido?
4. ¿Puedes interactuar sin lag?
```

### Test: Sin permisos
```
1. Settings → Permisos → Deniega LOCATION
2. Abre MAPA
3. ¿Ves marcadores? ✅ Debería sí
4. ¿"Mi ubicación" está vacío? ✅ Debería sí
5. Presiona "+" → Usa fallback a GDL ✅
```

---

## ✅ CHECKLIST FINAL

- [ ] TEST 1: Lugares cargan en mapa
- [ ] TEST 2: Info window aparece al tocar
- [ ] TEST 3: Distancia muestra en km
- [ ] TEST 4: Crear lugar pre-rellena coords
- [ ] TEST 4b: Nuevo lugar aparece en mapa
- [ ] TEST 5: "Ver detalles" funciona
- [ ] TEST 6: "Cerrar" funciona
- [ ] BONUS: Lugares sin coords no aparecen
- [ ] BONUS: Sin permisos sigue funcionando
- [ ] BONUS: Performance aceptable con muchos lugares

---

**Si todos los tests pasan: ✅ IMPLEMENTACIÓN EXITOSA**

Si hay fallos, reporta cuál test falla y qué ves en los logs.

