uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;
uniform mat4 modelMatrix;

attribute vec4 position;
attribute vec2 texCoordinate;

varying vec2 theTexCoordinate;

void main() {
   theTexCoordinate = texCoordinate;
   gl_Position = projectionMatrix * viewMatrix * modelMatrix * position;
}