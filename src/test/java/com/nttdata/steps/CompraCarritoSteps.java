package com.nttdata.steps;

import com.nttdata.core.DriverManager;
import com.nttdata.page.CompraCarritoPage;
import org.junit.Assert;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class CompraCarritoSteps {

    private final WebDriver driver;
    private final WebDriverWait wait;
    private final CompraCarritoPage page;

    private String nombreProductoSeleccionado;
    private double precioUnitario;
    private int cantidadSeleccionada;
    private double totalEsperado;

    public CompraCarritoSteps() {
        this.driver = DriverManager.getDriver();
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        this.page = new CompraCarritoPage();
    }

    public void openPage() {
        driver.get("https://qalab.bensg.com/store/pe/");
        driver.manage().window().maximize();
    }

    public void clickIniciarSesion() {
        WebElement btnLogin = wait.until(
                ExpectedConditions.elementToBeClickable(page.btnIniciarSesion)
        );
        clickConJs(btnLogin);
    }

    public void login(String usuario, String clave) {
        clickIniciarSesion();

        WebElement correo = wait.until(
                ExpectedConditions.visibilityOfElementLocated(page.txtCorreo)
        );
        correo.clear();
        correo.sendKeys(usuario);

        WebElement password = wait.until(
                ExpectedConditions.visibilityOfElementLocated(page.txtClave)
        );
        password.clear();
        password.sendKeys(clave);

        WebElement btnSubmit = wait.until(
                ExpectedConditions.elementToBeClickable(page.btnSubmitLogin)
        );
        clickConJs(btnSubmit);
    }

    public void validarAutenticacionExitosa() {
        wait.until(ExpectedConditions.or(
                ExpectedConditions.visibilityOfElementLocated(page.linkCuentaUsuario),
                ExpectedConditions.visibilityOfElementLocated(page.linkCerrarSesion)
        ));

        boolean cuentaVisible = !driver.findElements(page.linkCuentaUsuario).isEmpty();
        boolean logoutVisible = !driver.findElements(page.linkCerrarSesion).isEmpty();

        Assert.assertTrue("No se validó la autenticación", cuentaVisible || logoutVisible);
    }

    public void navegarCategoriaYSubcategoria(String categoria, String subcategoria) {
        ingresarCategoria(categoria);
        ingresarSubcategoria(subcategoria);
    }

    public void ingresarCategoria(String categoria) {
        WebElement cat;

        if (categoria.equalsIgnoreCase("Clothes")) {
            cat = wait.until(ExpectedConditions.visibilityOfElementLocated(page.categoriaClothes));
            scrollAlElemento(cat);
            clickConJs(cat);
            wait.until(ExpectedConditions.urlContains("3-clothes"));
        } else if (categoria.equalsIgnoreCase("Accesorios")) {
            cat = wait.until(ExpectedConditions.visibilityOfElementLocated(page.categoriaAccesorios));
            scrollAlElemento(cat);
            clickConJs(cat);
            wait.until(ExpectedConditions.urlContains("6-accessories"));
        } else {
            Assert.fail("Categoría no implementada: " + categoria);
            return;
        }

        String titulo = wait.until(
                ExpectedConditions.visibilityOfElementLocated(page.tituloPagina)
        ).getText().trim();

        Assert.assertEquals(
                "No se abrió la categoría correcta",
                categoria.toLowerCase(),
                normalizarTexto(titulo).toLowerCase()
        );
    }

    public void ingresarSubcategoria(String subcategoria) {
        WebElement subcat;

        if (subcategoria.equalsIgnoreCase("Men")) {
            subcat = wait.until(ExpectedConditions.visibilityOfElementLocated(page.subcategoriaMen));
            scrollAlElemento(subcat);
            clickConJs(subcat);
            wait.until(ExpectedConditions.urlContains("4-men"));
        } else if (subcategoria.equalsIgnoreCase("Home Accessories")) {
            subcat = wait.until(ExpectedConditions.visibilityOfElementLocated(page.subcategoriaHomeAccessories));
            scrollAlElemento(subcat);
            clickConJs(subcat);
            wait.until(ExpectedConditions.urlContains("8-home-accessories"));
        } else {
            Assert.fail("Subcategoría no implementada: " + subcategoria);
            return;
        }

        String titulo = wait.until(
                ExpectedConditions.visibilityOfElementLocated(page.tituloPagina)
        ).getText().trim();

        Assert.assertEquals(
                "No se abrió la subcategoría correcta",
                subcategoria.toLowerCase(),
                normalizarTexto(titulo).toLowerCase()
        );
    }

    public void agregarUnidadesDelPrimerProducto(int cantidad) {
        cantidadSeleccionada = cantidad;

        WebElement producto = wait.until(
                ExpectedConditions.visibilityOfElementLocated(page.primerProductoListado)
        );

        nombreProductoSeleccionado = normalizarTexto(producto.getText());
        scrollAlElemento(producto);
        clickConJs(producto);

        String tituloDetalle = wait.until(
                ExpectedConditions.visibilityOfElementLocated(page.tituloDetalleProducto)
        ).getText().trim();

        Assert.assertEquals(
                "No se abrió el detalle correcto del producto",
                nombreProductoSeleccionado.toLowerCase(),
                normalizarTexto(tituloDetalle).toLowerCase()
        );

        String precioTexto = wait.until(
                ExpectedConditions.visibilityOfElementLocated(page.precioUnitarioDetalle)
        ).getText().trim();

        precioUnitario = convertirPrecioANumero(precioTexto);
        totalEsperado = precioUnitario * cantidadSeleccionada;

        WebElement inputCantidad = wait.until(
                ExpectedConditions.visibilityOfElementLocated(page.inputCantidadDetalle)
        );

        inputCantidad.sendKeys(Keys.CONTROL + "a");
        inputCantidad.sendKeys(String.valueOf(cantidadSeleccionada));

        WebElement btnAgregar = wait.until(
                ExpectedConditions.elementToBeClickable(page.btnAgregarAlCarritoDetalle)
        );

        scrollAlElemento(btnAgregar);
        clickConJs(btnAgregar);
    }

    public void validarMensajeProductoAgregado() {
        wait.until(ExpectedConditions.visibilityOfElementLocated(page.modalProductoAgregado));

        String textoModal = wait.until(
                ExpectedConditions.visibilityOfElementLocated(page.lblModalProductoAgregado)
        ).getText().trim();

        Assert.assertTrue(
                "No se mostró el mensaje de producto agregado",
                textoModal.contains("Producto añadido") ||
                        textoModal.contains("Product successfully added")
        );
    }

    public void validarMontoTotalEnPopup() {
        wait.until(ExpectedConditions.visibilityOfElementLocated(page.modalProductoAgregado));

        String nombreModal = wait.until(
                ExpectedConditions.visibilityOfElementLocated(page.lblNombreProductoModal)
        ).getText().trim();

        Assert.assertEquals(
                "El nombre del producto en el popup no coincide",
                nombreProductoSeleccionado.toLowerCase(),
                normalizarTexto(nombreModal).toLowerCase()
        );

        String cantidadModal = wait.until(
                ExpectedConditions.visibilityOfElementLocated(page.lblCantidadProductoModal)
        ).getText().trim();

        Assert.assertEquals(
                "La cantidad en el popup no coincide",
                String.valueOf(cantidadSeleccionada),
                cantidadModal
        );

        String totalModalTexto = wait.until(
                ExpectedConditions.visibilityOfElementLocated(page.lblTotalModal)
        ).getText().trim();

        double totalModal = convertirPrecioANumero(totalModalTexto);

        Assert.assertEquals(
                "El monto total del popup no fue calculado correctamente",
                redondear(totalEsperado),
                redondear(totalModal),
                0.01
        );
    }

    public void ingresarAlCarrito() {
        WebElement btnCarrito = wait.until(
                ExpectedConditions.visibilityOfElementLocated(page.btnIrAlCarritoDesdeModal)
        );

        scrollAlElemento(btnCarrito);
        clickConJs(btnCarrito);
    }

    public void validarTituloPaginaCarrito() {
        String titulo = wait.until(
                ExpectedConditions.visibilityOfElementLocated(page.tituloCarrito)
        ).getText().trim();

        Assert.assertTrue(
                "El título de la página del carrito no es correcto",
                titulo.equalsIgnoreCase("Carrito") || titulo.equalsIgnoreCase("Shopping Cart")
        );
    }

    public void validarCalculoPreciosEnCarrito() {
        String nombreCarrito = wait.until(
                ExpectedConditions.visibilityOfElementLocated(page.lblProductoCarrito)
        ).getText().trim();

        Assert.assertEquals(
                "El producto del carrito no coincide",
                nombreProductoSeleccionado.toLowerCase(),
                normalizarTexto(nombreCarrito).toLowerCase()
        );

        String cantidadCarrito = wait.until(
                ExpectedConditions.visibilityOfElementLocated(page.txtCantidadCarrito)
        ).getAttribute("value").trim();

        Assert.assertEquals(
                "La cantidad del carrito no coincide",
                String.valueOf(cantidadSeleccionada),
                cantidadCarrito
        );

        String subtotalTexto = wait.until(
                ExpectedConditions.visibilityOfElementLocated(page.lblSubtotalCarrito)
        ).getText().trim();

        double subtotalCarrito = convertirPrecioANumero(subtotalTexto);

        Assert.assertEquals(
                "El cálculo de precios en el carrito no es correcto",
                redondear(totalEsperado),
                redondear(subtotalCarrito),
                0.01
        );
    }

    // Prueba de salud: verifica que el navegador recibió la página principal.
    public void validarTiendaDisponible() {
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.tagName("body")));
        String url = driver.getCurrentUrl().toLowerCase();
        String contenido = driver.findElement(By.tagName("body")).getText().trim();
        Assert.assertTrue("La tienda no abrió su dominio esperado", url.contains("qalab.bensg.com"));
        Assert.assertFalse("La tienda cargó un documento vacío", contenido.isEmpty());
        Assert.assertTrue("No aparece el acceso a iniciar sesión", !driver.findElements(page.btnIniciarSesion).isEmpty());
    }

    // Es una prueba indirecta del catálogo respaldado por BD: no consulta la BD directamente.
    public void validarListadoProductos() {
        String nombre = wait.until(ExpectedConditions.visibilityOfElementLocated(page.primerProductoListado)).getText().trim();
        Assert.assertFalse("El primer producto no muestra su nombre", nombre.isEmpty());
        WebElement precio = wait.until(ExpectedConditions.visibilityOfElementLocated(page.precioPrimerProductoListado));
        Assert.assertTrue("El primer producto no muestra precio válido", convertirPrecioANumero(precio.getText()) > 0);
    }

    // Usa un usuario inexistente para evitar bloquear cuentas reales.
    public void intentarLoginInvalido() {
        login("prueba-incorrecta-ntt@example.invalid", "PasswordIncorrecto987!NTT");
    }

    public void validarAutenticacionRechazada() {
        // Aguardar la respuesta explícita del sitio (no aprobar solo porque el formulario aún se ve).
        WebElement mensaje = wait.until(navegador -> {
            for (WebElement candidato : navegador.findElements(page.mensajeErrorLogin)) {
                if (candidato.isDisplayed() && !candidato.getText().trim().isEmpty()) {
                    return candidato;
                }
            }
            return null;
        });
        Assert.assertFalse("No hay un mensaje de rechazo de autenticación", mensaje.getText().trim().isEmpty());
        Assert.assertTrue("Se inició sesión con credenciales inválidas",
                driver.findElements(page.linkCerrarSesion).isEmpty());
        Assert.assertTrue("El formulario de acceso desapareció tras el rechazo",
                !driver.findElements(page.txtCorreo).isEmpty());
    }

    private String categoriaInexistenteBuscada;
    private String urlAnteriorCategoria;
    private boolean categoriaInexistenteVisible;

    public void intentarNavegarCategoriaInexistente(String categoria) {
        categoriaInexistenteBuscada = categoria;
        urlAnteriorCategoria = driver.getCurrentUrl();
        categoriaInexistenteVisible = false;
        // Se evalúa únicamente la navegación real; nunca se inventan URLs de categorías.
        for (WebElement enlace : driver.findElements(By.cssSelector("header a, nav a, #top-menu a"))) {
            if (enlace.isDisplayed() && normalizarTexto(enlace.getText()).equalsIgnoreCase(categoria)) {
                categoriaInexistenteVisible = true;
                break;
            }
        }
    }

    public void validarCategoriaInexistente(String categoria) {
        Assert.assertEquals("Se verificó una categoría diferente", categoria, categoriaInexistenteBuscada);
        Assert.assertFalse("La categoría inexistente está disponible en el menú: " + categoria,
                categoriaInexistenteVisible);
        Assert.assertEquals("El navegador cambió de página inesperadamente",
                urlAnteriorCategoria, driver.getCurrentUrl());
        // El escenario termina aquí: no se intenta añadir ningún producto al carrito.
    }

    private void scrollAlElemento(WebElement elemento) {
        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].scrollIntoView({block:'center'});",
                elemento
        );
    }

    private void clickConJs(WebElement elemento) {
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", elemento);
    }

    private String normalizarTexto(String texto) {
        return texto
                .replace('\u00A0', ' ')
                .replaceAll("\\s+", " ")
                .trim();
    }

    private double convertirPrecioANumero(String textoPrecio) {
        String limpio = normalizarTexto(textoPrecio)
                .replace("S/", "")
                .replace("$", "")
                .replace(",", "")
                .trim();
        return Double.parseDouble(limpio);
    }

    private double redondear(double valor) {
        return Math.round(valor * 100.0) / 100.0;
    }
}