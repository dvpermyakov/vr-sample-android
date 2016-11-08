precision mediump float;

uniform sampler2D texture;

varying vec2 theTexCoordinate;

void main() {
    gl_FragColor = texture2D(texture, theTexCoordinate);
}