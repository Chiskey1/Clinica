package Controlador;

public final class AppContext {
    // Instancia estática que guarda el controlador principal de la aplicación
    private static AppController CONTROLLER;

    // Constructor privado para evitar instanciación (clase singleton)
    private AppContext() {}

    // Inicializa el contexto con un controlador, debe llamarse al inicio de la app
    public static void init(AppController controller) {
        CONTROLLER = controller;
    }

    // Devuelve el controlador almacenado, lanza error si no ha sido inicializado
    public static AppController get() {
        if (CONTROLLER == null)
            throw new IllegalStateException("AppContext no inicializado. Llama AppContext.init(...) en el menú principal.");
        return CONTROLLER;
    }
}
