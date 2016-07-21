precision mediump float;
varying vec4 v_Color;
varying vec3 v_Grid;

void main() {
    float depth = gl_FragCoord.z / gl_FragCoord.w; // Calculate world-space distance.

    if ((mod(abs(v_Grid.x), 3.0) < 1.0) || (mod(abs(v_Grid.z), 3.0) < 1.0)) {
        gl_FragColor = v_Color;
    } else {
        gl_FragColor = vec4(1.0, 1.0, 1.0, 1.0);
    }
}
