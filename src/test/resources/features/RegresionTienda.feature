@regresion
Feature: Verificacion de disponibilidad, autenticacion y catalogo de QALAB
  Como automatizador de NTT quiero detectar caidas del sitio, fallos de autenticacion
  y problemas de visualizacion de productos.

  @disponibilidad @catalogo
  Scenario: La tienda se encuentra disponible y lista productos
    Given estoy en la página de la tienda
    Then verifico que la tienda se encuentra disponible
    When navego a la categoria "Clothes" y subcategoria "Men"
    Then verifico que se muestran productos con nombre y precio

  @negativo @autenticacion
  Scenario: Credenciales invalidas no permiten acceder a la cuenta
    Given estoy en la página de la tienda
    When intento iniciar sesión con credenciales inválidas
    Then verifico que se rechaza la autenticación y no accedo a mi cuenta

  @negativo @catalogo
  Scenario: Una categoria inexistente no permite continuar el flujo
    Given estoy en la página de la tienda
    And me logueo con mis credenciales configuradas
    When intento navegar a la categoria inexistente "Autos"
    Then verifico que la categoria "Autos" no existe y el flujo no continua
