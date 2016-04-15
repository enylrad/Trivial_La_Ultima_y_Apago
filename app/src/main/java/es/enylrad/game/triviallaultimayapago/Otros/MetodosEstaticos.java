package es.enylrad.game.triviallaultimayapago.Otros;

import android.animation.ValueAnimator;
import android.widget.TextView;

/**
 * Clase que tiene varios metodos estaticos
 */
public class MetodosEstaticos {

    /**
     * Incrementa con una animación una cifra
     *
     * @param num_ini  cifra inicial
     * @param num_fin  cifra final
     * @param duracion duración
     * @param textView textview a modificar
     */
    public static void iniciarAnimacionContar(int num_ini, int num_fin, int duracion, final TextView textView) {
        ValueAnimator animator = new ValueAnimator();
        animator.setObjectValues(num_ini, num_fin);
        animator.setDuration(duracion);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            public void onAnimationUpdate(ValueAnimator animation) {
                textView.setText(String.valueOf((int) animation.getAnimatedValue()));
            }
        });
        animator.start();
    }

    /**
     * Incrementa con una animación una cifra
     *
     * @param num_ini  cifra inicial
     * @param num_fin  cifra final
     * @param duracion duración
     * @param textView textview a modificar
     * @param texto    texto añadido
     */
    public static void iniciarAnimacionContar(int num_ini, int num_fin, int duracion, final TextView textView, final String texto) {
        ValueAnimator animator = new ValueAnimator();
        animator.setObjectValues(num_ini, num_fin);
        animator.setDuration(duracion);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            public void onAnimationUpdate(ValueAnimator animation) {
                textView.setText(String.valueOf((int) animation.getAnimatedValue() + texto));
            }
        });
        animator.start();
    }
}
