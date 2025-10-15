document.addEventListener('DOMContentLoaded', () => {
    const productList = document.getElementById('product-list');
    const cartItems = document.getElementById('cart-items');
    const cartTotal = document.getElementById('cart-total');
    const checkoutBtn = document.getElementById('checkout-btn');

    let miCarrito = null;
    let carritoId = null;

    // 1. Crear carrito al cargar la página
    async function crearCarrito() {
        try {
            const response = await fetch('http://161.153.217.110:18082/shopping-cart/api/carrito', { method: 'POST' });
            miCarrito = await response.json();
            carritoId = miCarrito.id;
            console.log('Carrito creado:', carritoId);
        } catch (error) {
            console.error('Error al crear carrito:', error);
        }
    }

    // 2. Cargar productos desde la API
    async function fetchProducts() {
        try {
            const response = await fetch('http://161.153.217.110:18082/shopping-cart/api/articulos');
            const products = await response.json();
            
            products.forEach(product => {
                // Verificar que el producto tenga los datos necesarios
                if (!product || !product.nombre || !product.proveedor) {
                    console.warn('Producto con datos incompletos:', product);
                    return;
                }
                
                const productCard = `
                    <div class="col-md-4 mb-4">
                        <div class="card">
                            <div class="card-body">
                                <h5 class="card-title">${product.nombre}</h5>
                                <p class="card-text">$${product.precio ? product.precio.toFixed(2) : 'N/A'}</p>
                                <p class="card-text"><small class="text-muted">Proveedor: ${product.proveedor.nombre}</small></p>
                                <div class="d-flex justify-content-between">
                                    <button class="btn btn-success" onclick="addToCart('${product.id}', '${product.nombre}', ${product.precio || 0})">
                                        Agregar al Carrito
                                    </button>
                                    <button class="btn btn-info" onclick="showSupplierMap(${JSON.stringify(product.proveedor).replace(/"/g, '&quot;')})">
                                        <i class="fas fa-map-marker-alt"></i> Ver Ubicación
                                    </button>
                                </div>
                            </div>
                        </div>
                    </div>
                `;
                productList.innerHTML += productCard;
            });
        } catch (error) {
            console.error('Error al cargar productos:', error);
        }
    }

    // 2. Función para agregar al carrito
    window.addToCart = async function(id, nombre, precio) {
        if (!carritoId) {
            alert('Carrito no creado aún.');
            return;
        }
        try {
            const response = await fetch(`http://161.153.217.110:18082/shopping-cart/api/carrito/${carritoId}/articulo/${id}`, { method: 'POST' });
            if (response.ok) {
                miCarrito = await fetch(`http://161.153.217.110:18082/shopping-cart/api/carrito/${carritoId}`).then(r => r.json());
                updateCartView();
            } else {
                alert('Error al agregar artículo.');
            }
        } catch (error) {
            console.error('Error:', error);
        }
    };

    // 3. Actualizar la vista del carrito
    function updateCartView() {
        if (!miCarrito) return;
        cartItems.innerHTML = '';
        let total = 0;
        miCarrito.detalles.forEach(detalle => {
            const articulo = detalle.articulo;
            const li = document.createElement('li');
            li.className = 'list-group-item d-flex justify-content-between align-items-center';
            li.textContent = `${articulo.nombre} (x${detalle.cantidad})`;
            const span = document.createElement('span');
            span.className = 'badge badge-primary badge-pill';
            span.textContent = `$${(articulo.precio * detalle.cantidad).toFixed(2)}`;
            li.appendChild(span);
            cartItems.appendChild(li);
            total += articulo.precio * detalle.cantidad;
        });
        cartTotal.textContent = total.toFixed(2);
    }
    
    // 4. Lógica para finalizar la compra
    checkoutBtn.addEventListener('click', async () => {
        if (!carritoId) {
            alert('No hay carrito para procesar.');
            return;
        }
        
        try {
            const response = await fetch(`http://161.153.217.110:18082/shopping-cart/api/carrito/${carritoId}/checkout`, { method: 'POST' });
            if(response.ok) {
                alert('¡Compra procesada con éxito!');
                miCarrito = null;
                carritoId = null;
                updateCartView();
                crearCarrito(); // Crear nuevo carrito
            } else {
                alert('Error al procesar la compra.');
            }
        } catch (error) {
            console.error('Error en el checkout:', error);
        }
    });

    fetchProducts();
    crearCarrito();

    // 5. Inicializar mapa y cargar proveedores
    initMap();
});

async function initMap() {
    // Inicializar mapa centrado en Argentina
    const map = L.map('map').setView([-34.0, -64.0], 5);

    // Agregar capa de OpenStreetMap
    L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
        attribution: '© OpenStreetMap contributors'
    }).addTo(map);

    // Cargar proveedores y agregar marcadores
    try {
        const response = await fetch('http://161.153.217.110:18082/shopping-cart/api/proveedores');
        const proveedores = await response.json();

        proveedores.forEach(proveedor => {
            if (proveedor.latitud && proveedor.longitud) {
                L.marker([proveedor.latitud, proveedor.longitud])
                    .addTo(map)
                    .bindPopup(`<b>${proveedor.nombre}</b><br>${proveedor.direccion}`);
            }
        });
    } catch (error) {
        console.error('Error al cargar proveedores:', error);
    }
}

let supplierMap = null;

window.showSupplierMap = function(proveedor) {
    if (!proveedor || !proveedor.latitud || !proveedor.longitud) {
        alert('Información de ubicación del proveedor no disponible.');
        return;
    }
    
    $('#supplierMapModal').modal('show');
    
    // Si el mapa ya existe, destruirlo primero
    if (supplierMap) {
        supplierMap.remove();
    }
    
    // Inicializar mapa centrado en el proveedor
    supplierMap = L.map('supplier-map').setView([proveedor.latitud, proveedor.longitud], 13);
    
    // Agregar capa de OpenStreetMap
    L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
        attribution: '© OpenStreetMap contributors'
    }).addTo(supplierMap);
    
    // Agregar marcador del proveedor
    L.marker([proveedor.latitud, proveedor.longitud])
        .addTo(supplierMap)
        .bindPopup(`<b>${proveedor.nombre || 'Proveedor'}</b><br>${proveedor.direccion || 'Dirección no disponible'}`)
        .openPopup();
}

// Limpiar mapa cuando se cierra el modal
$('#supplierMapModal').on('hidden.bs.modal', function () {
    if (supplierMap) {
        supplierMap.remove();
        supplierMap = null;
    }
});