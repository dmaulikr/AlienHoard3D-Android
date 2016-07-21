uniform mat4 u_MVPMatrix2;

attribute vec4 a_Position2;

void main()
{
    gl_Position = u_MVPMatrix2 * a_Position2;
    gl_PointSize = 5.0;
}