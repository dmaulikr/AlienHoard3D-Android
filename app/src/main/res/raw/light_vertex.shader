uniform mat4 u_Model;
uniform mat4 u_MVP;
uniform mat4 u_MVMatrix;
//uniform vec3 u_LightPos;

attribute vec4 a_Position;
attribute vec4 a_Color;
attribute vec3 a_Normal;
attribute vec2 a_TexCoordinate;

//varying vec4 v_Color;
varying vec3 v_Grid;
varying vec2 v_TexCoordinate;

varying vec3 v_Position;       // This will be passed into the fragment shader.
varying vec4 v_Color;          // This will be passed into the fragment shader.
varying vec3 v_Normal;         // This will be passed into the fragment shader.



void main() {
    v_Grid = vec3(u_Model * a_Position);

    vec3 modelViewVertex = vec3(u_MVMatrix * a_Position);
    vec3 modelViewNormal = vec3(u_MVMatrix * vec4(a_Normal, 0.0));

 //  float distance = length(u_LightPos - modelViewVertex);
 //  vec3 lightVector = normalize(u_LightPos - modelViewVertex);
 //  float diffuse = max(dot(modelViewNormal, lightVector), 0.1);

  // diffuse = diffuse * (1.0 / (1.0 + (0.001 * distance * distance)));
 //  v_Color = a_Color * diffuse;
 //  gl_Position = u_MVP * a_Position;

    // Transform the vertex into eye space.
    v_Position = vec3(u_MVMatrix * a_Position);

    // Pass through the color.
    v_Color = a_Color;

    // Pass through the texture coordinate.
    v_TexCoordinate = a_TexCoordinate;

    // Transform the normal's orientation into eye space.
    v_Normal = vec3(u_MVMatrix * vec4(a_Normal, 0.0));

    // gl_Position is a special variable used to store the final position.
    // Multiply the vertex by the matrix to get the final point in normalized screen coordinates.
    gl_Position = u_MVP * a_Position;
}
