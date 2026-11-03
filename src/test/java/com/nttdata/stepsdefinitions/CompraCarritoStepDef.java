package com.nttdata.stepsdefinitions;

import com.nttdata.steps.CompraCarritoSteps;
import io.cucumber.java.en.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class CompraCarritoStepDef {

    CompraCarritoSteps compraSteps = new CompraCarritoSteps();

    @Given("estoy en la página de la tienda")
    public void estoyEnLaPaginaDeLaTienda() {
        compraSteps.openPage();
    }

    @And("me logueo con mis credenciales configuradas")
    public void meLogueoConMisCredencialesConfiguradas() {
        // Mantiene las credenciales de prueba originales en qalab.properties.
        // Opcionalmente se pueden sobrescribir mediante variables de entorno.
        Properties config = new Properties();
        try (InputStream archivo = getClass().getClassLoader().getResourceAsStream("qalab.properties")) {
            if (archivo == null) {
                throw new IllegalStateException("Falta src/test/resources/qalab.properties");
            }
            config.load(archivo);
        } catch (IOException e) {
            throw new IllegalStateException("No se pudo leer qalab.properties", e);
        }

        String usuario = System.getenv("QALAB_USER");
        if (usuario == null || usuario.trim().isEmpty()) {
            usuario = System.getProperty("qalab.user", config.getProperty("qalab.user", ""));
        }
        String clave = System.getenv("QALAB_PASSWORD");
        if (clave == null || clave.trim().isEmpty()) {
            clave = System.getProperty("qalab.password", config.getProperty("qalab.password", ""));
        }
        if (usuario.trim().isEmpty() || clave.trim().isEmpty()) {
            throw new IllegalStateException("Configura las credenciales de QALAB antes de ejecutar la prueba");
        }

        compraSteps.login(usuario, clave);
        compraSteps.validarAutenticacionExitosa();
    }

    @Then("verifico que la tienda se encuentra disponible")
    public void verificoQueLaTiendaSeEncuentraDisponible() {
        compraSteps.validarTiendaDisponible();
    }

    @Then("verifico que se muestran productos con nombre y precio")
    public void verificoQueSeMuestranProductosConNombreYPrecio() {
        compraSteps.validarListadoProductos();
    }

    @When("intento iniciar sesión con credenciales inválidas")
    public void intentoIniciarSesionConCredencialesInvalidas() {
        compraSteps.intentarLoginInvalido();
    }

    @Then("verifico que se rechaza la autenticación y no accedo a mi cuenta")
    public void verificoQueSeRechazaLaAutenticacion() {
        compraSteps.validarAutenticacionRechazada();
    }

    @When("intento navegar a la categoria inexistente {string}")
    public void intentoNavegarCategoriaInexistente(String categoria) {
        compraSteps.intentarNavegarCategoriaInexistente(categoria);
    }

    @Then("verifico que la categoria {string} no existe y el flujo no continua")
    public void verificoCategoriaInexistente(String categoria) {
        compraSteps.validarCategoriaInexistente(categoria);
    }

    @When("navego a la categoria {string} y subcategoria {string}")
    public void navegoALaCategoriaYSubcategoria(String categoria, String subcategoria) {
        compraSteps.navegarCategoriaYSubcategoria(categoria, subcategoria);
    }

    @And("agrego {int} unidades del primer producto al carrito")
    public void agregoUnidadesDelPrimerProductoAlCarrito(int cantidad) {
        compraSteps.agregarUnidadesDelPrimerProducto(cantidad);
    }

    @Then("valido en el popup la confirmación del producto agregado")
    public void validoEnElPopupLaConfirmacionDelProductoAgregado() {
        compraSteps.validarMensajeProductoAgregado();
    }

    @And("valido en el popup que el monto total sea calculado correctamente")
    public void validoEnElPopupQueElMontoTotalSeaCalculadoCorrectamente() {
        compraSteps.validarMontoTotalEnPopup();
    }

    @When("ingreso a la página del carrito")
    public void ingresoALaPaginaDelCarrito() {
        compraSteps.ingresarAlCarrito();
    }

    @Then("valido el titulo de la pagina del carrito")
    public void validoElTituloDeLaPaginaDelCarrito() {
        compraSteps.validarTituloPaginaCarrito();
    }

    @And("vuelvo a validar el calculo de precios en el carrito")
    public void vuelvoAValidarElCalculoDePreciosEnElCarrito() {
        compraSteps.validarCalculoPreciosEnCarrito();
    }
}