
## Ejecución desde IntelliJ

1. Abrir la carpeta del proyecto (donde está `pom.xml`).
2. Esperar la carga de Maven sin modificar la configuración que ya funciona.
3. Ejecutar `src/test/java/com/nttdata/runner/RunnerTest.java`.
4. Revisar el PDF y el HTML en `target/cucumber/`.

Desde consola Windows con Maven instalado:

```powershell
mvn -Dtest=RunnerTest test
```

Para ejecutar únicamente la compra original:

```powershell
mvn -Dtest=RunnerTest "-Dcucumber.filter.tags=@compraCarrito" test
```
## Reporte automático PDF

Cucumber genera después de cada ejecución, también si hay fallos:

- `target/cucumber/cucumber-report.html` (pasos, capturas de error).
- `target/cucumber/cucumber.json` (resultado estructurado).
- `target/cucumber/reporte-regresion.pdf` (resumen por escenario, con fallos).

El PDF se genera con **Java puro**, sin instalar librerías adicionales ni alterar `pom.xml`.
Las capturas de pantalla se adjuntan al reporte Cucumber **cuando falla un escenario**.

# Automatización Web - Jean Sernaque

Proyecto de automatización web desarrollado con:
- Java
- Maven
- Selenium WebDriver
- Cucumber
- JUnit

## Estructura del proyecto
- src/test/java/com/nttdata/core
- src/test/java/com/nttdata/page
- src/test/java/com/nttdata/steps
- src/test/java/com/nttdata/stepsdefinitions
- src/test/resources/features

## Ejecución
1. Clonar el repositorio
2. Abrir el proyecto en IntelliJ
3. Esperar que Maven descargue dependencias
4. Ejecutar el RunnerTest

## Notas
- El proyecto automatiza el flujo de compra en carrito.
- Se usa patrón Page Object.
- Las pruebas están escritas con Cucumber.

## Alcance del paso de carrito

El paso `ingreso a la página del carrito` hace clic en el botón del popup que
abre el carrito. **No realiza checkout ni registra un pedido.**