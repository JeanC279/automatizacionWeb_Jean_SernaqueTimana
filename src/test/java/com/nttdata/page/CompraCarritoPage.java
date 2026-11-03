package com.nttdata.page;

import org.openqa.selenium.By;

public class CompraCarritoPage {

    // LOGIN
    public By btnIniciarSesion = By.xpath("//a[contains(@href,'iniciar-sesion') and contains(normalize-space(.),'Iniciar sesión')]");
    public By txtCorreo = By.id("field-email");
    public By txtClave = By.id("field-password");
    public By btnSubmitLogin = By.id("submit-login");

    // MENSAJES DE ERROR EN EL LOGIN (PrestaShop)
    public By mensajeErrorLogin = By.cssSelector("#notifications .alert-danger, .alert-danger, #login-form .help-block, .js-error, .invalid-feedback");

    // AUTENTICACION
    public By linkCuentaUsuario = By.xpath("//a[contains(@class,'account')]");
    public By linkCerrarSesion = By.xpath("//a[contains(@href,'logout') or contains(normalize-space(.),'Cerrar sesión')]");

    // CATEGORIAS PRINCIPALES
    public By categoriaClothes = By.xpath("(//a[contains(@href,'/3-clothes') and contains(normalize-space(.),'Clothes')])[1]");
    public By categoriaAccesorios = By.xpath("(//a[contains(@href,'/6-accessories') and contains(normalize-space(.),'Accesorios')])[1]");

    // SUBCATEGORIAS
    public By subcategoriaMen = By.xpath("//div[@id='subcategories']//a[contains(@href,'/4-men') and contains(normalize-space(.),'Men')]");
    public By subcategoriaHomeAccessories = By.xpath("//div[@id='subcategories']//h5/a[contains(@href,'/8-home-accessories') and contains(normalize-space(.),'Home Accessories')]");

    // TITULOS
    public By tituloPagina = By.xpath("//h1");

    // LISTADO DE PRODUCTOS
    public By primerProductoListado = By.xpath("(//div[@id='js-product-list']//article//h2/a)[1]");

    public By precioPrimerProductoListado = By.cssSelector("#js-product-list article .price");

    // DETALLE DEL PRODUCTO
    public By tituloDetalleProducto = By.xpath("//h1");
    public By precioUnitarioDetalle = By.xpath("(//div[contains(@class,'product-prices')]//span[contains(@class,'current-price-value')])[1]");
    public By inputCantidadDetalle = By.id("quantity_wanted");
    public By btnAgregarAlCarritoDetalle = By.xpath("//button[contains(@class,'add-to-cart') and contains(.,'Añadir al carrito')]");

    // MODAL PRODUCTO AGREGADO
    public By modalProductoAgregado = By.id("blockcart-modal");
    public By lblModalProductoAgregado = By.xpath("//div[@id='blockcart-modal']//h4");
    public By lblNombreProductoModal = By.xpath("//div[@id='blockcart-modal']//h6[contains(@class,'product-name')]");
    public By lblCantidadProductoModal = By.xpath("//div[@id='blockcart-modal']//span[contains(.,'Cantidad')]/strong");
    public By lblPrecioUnitarioModal = By.xpath("//div[@id='blockcart-modal']//span[contains(.,'Precio')]/strong");
    public By lblTotalModal = By.xpath("//div[@id='blockcart-modal']//p[contains(@class,'product-total')]//span[@class='value']");
    public By btnIrAlCarritoDesdeModal = By.xpath("//div[@id='blockcart-modal']//a[contains(@href,'cart') or contains(@href,'carrito')]");

    // CARRITO
    public By tituloCarrito = By.xpath("//h1[contains(normalize-space(.),'Carrito') or contains(normalize-space(.),'Shopping Cart')]");
    public By lblProductoCarrito = By.xpath("(//div[contains(@class,'product-line-info')]//a)[1]");
    public By txtCantidadCarrito = By.xpath("(//input[contains(@class,'js-cart-line-product-quantity')])[1]");

    // Total / subtotal de la línea del producto en el carrito
    public By lblSubtotalCarrito = By.xpath(
            "(//div[contains(@class,'product-line-grid-right')]//strong[contains(@class,'product-line-total')] | " +
                    "//div[contains(@class,'product-line-grid-right')]//span[contains(@class,'product-price') or contains(@class,'price')])[last()]"
    );
}