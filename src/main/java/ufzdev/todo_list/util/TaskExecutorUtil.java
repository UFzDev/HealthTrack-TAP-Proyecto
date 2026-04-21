package ufzdev.todo_list.util;

import javafx.concurrent.Task;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

public class TaskExecutorUtil {
    // Creamos 4 hilos para tareas en segundo plano
    private static final ExecutorService executor = Executors.newFixedThreadPool(4, runnable -> {
        Thread t = new Thread(runnable);
        t.setDaemon(true); // Cierra los hilos al cerrar la aplicación
        return t;
    });

    /**
     * Ejecuta una tarea y devuelve el control al programador para el éxito o error.
     * @param <V> Tipo de dato que devuelve la tarea.
     * @param action El código que corre en segundo plano.
     * @param onSuccess Qué hacer al terminar
     * @param onFailure Qué hacer si falla
     */
    public static <V> void execute(Callable<V> action, Consumer<V> onSuccess, Consumer<Throwable> onFailure) {
        Task<V> task = new Task<>() {
            @Override
            protected V call() throws Exception {
                return action.call();
            }
        };


        // Al terminar, ejecutamos lo que se mandó por parámetro
        task.setOnSucceeded(e -> onSuccess.accept(task.getValue()));
        task.setOnFailed(e -> onFailure.accept(task.getException()));

        executor.execute(task);
    }
}