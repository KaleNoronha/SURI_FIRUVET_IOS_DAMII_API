# Nuevo Endpoint: GET /api/clientes/uid/{uid}

## Fecha
2026-07-01

## Cambio realizado

Se agregó un nuevo endpoint REST que permite buscar un cliente por su Firebase UID directamente, sin necesidad de descargar toda la lista de clientes.

### Endpoint

```
GET /api/clientes/uid/{uid}
```

### Respuestas

| Código | Descripción |
|--------|-------------|
| 200    | Cliente encontrado — retorna el `ClienteDTO` |
| 404    | No existe un cliente con ese UID |

### Ejemplo

```bash
curl https://suri-firuvet-ios-damii-api.onrender.com/api/clientes/uid/abc123FirebaseUid
```

Respuesta (200):
```json
{
  "id": 5,
  "nombCli": "Kale",
  "apeCli": "N",
  "fecNac": "01/01/2000",
  "uid": "abc123FirebaseUid",
  "idRol": 1,
  "rolNombre": "usuario"
}
```

## ¿Por qué se hizo este cambio?

### Problema

El frontend (`suri-firuvet-web`) necesita obtener los datos del cliente logueado cada vez que Firebase Auth detecta un cambio de sesión. Antes, el único endpoint disponible era:

```
GET /api/clientes  →  retorna TODOS los clientes
```

El frontend hacía:
```typescript
const clientes = await clienteService.getAll(); // descarga TODOS
const found = clientes.find(c => c.uid === firebaseUser.uid); // busca 1
```

Esto es un **escaneo O(n)** — con 10 clientes es invisible, pero con 1000+ clientes:
- Se descarga data innecesaria por la red (payload grande)
- El servidor carga todos los registros en memoria
- Cada login/refresh repite este proceso
- Escala linealmente: más usuarios = más lento para todos

### Solución

El nuevo endpoint `GET /api/clientes/uid/{uid}` ejecuta la búsqueda en la base de datos con un `WHERE uid = :uid`, que es **O(1)** si hay un índice en la columna `uid` (o O(log n) con el índice B-tree por defecto de PostgreSQL).

**Complejidad anterior:** O(n) en red + O(n) en memoria del frontend
**Complejidad nueva:** O(1) consulta directa a la BD

### Impacto en el frontend

El `AuthProvider` en `src/auth/index.tsx` del frontend puede ahora reemplazar:

```typescript
// ANTES (O(n) — descarga todos)
const clientes = await clienteService.getAll();
const found = clientes.find(c => c.uid === firebaseUser.uid);
```

Con:

```typescript
// DESPUÉS (O(1) — busca directo)
const found = await clienteService.getByUid(firebaseUser.uid);
```

### Archivos modificados

| Archivo | Cambio |
|---------|--------|
| `ClienteService.java` | Nuevo método `getByUid(String uid)` |
| `ClienteResource.java` | Nuevo endpoint `GET /api/clientes/uid/{uid}` |

### Recomendación adicional

Para máxima performance, agregar un índice único en la columna `uid`:

```sql
CREATE UNIQUE INDEX idx_cliente_uid ON cliente(uid);
```

Esto garantiza búsquedas en O(1) amortizado y previene duplicados a nivel de base de datos.
