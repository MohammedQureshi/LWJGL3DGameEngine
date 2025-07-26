#version 400 core

in vec3 position;
in vec2 textureCoords;
in vec3 normal;

out vec2 pass_textureCoords;
out vec3 surfaceNormal;
out vec3 toLightVector;
out vec3 toCameraVector;

uniform mat4 transformationMatrix;
uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;
uniform vec3 lightPosition;
uniform float time;
uniform float speed;
uniform float frequency;
uniform float swayStrength;
uniform float swayOffset;

uniform float useFakeLighting;
uniform float useFakeWind;

void main(void) {

    vec3 pos = position;

    if (useFakeWind > 0.5) {
        float heightFactor = clamp(pos.y, 0.0, 1.0);
        float wave = sin((pos.z * 0.5 + time * speed + swayOffset) * frequency)
        + cos((pos.x * 0.5 + time * speed + swayOffset) * frequency * 0.7);

        float gust = sin(time * 0.1) * 0.5 + 0.5;
        float swayAmount = wave * swayStrength * heightFactor * gust;

        pos.x += swayAmount * 0.6;
        pos.z += swayAmount * 0.4;
    }


    vec4 worldPosition = transformationMatrix * vec4(pos, 1.0);
    gl_Position = projectionMatrix * viewMatrix * worldPosition;
    pass_textureCoords = textureCoords;

    vec3 actualNormal = normal;
    if (useFakeLighting > 0.5) {
        actualNormal = vec3(0.0, 1.0, 0.0);
    }

    surfaceNormal = normalize((transformationMatrix * vec4(actualNormal, 0.0)).xyz);
    toLightVector = lightPosition - worldPosition.xyz;
    toCameraVector = (inverse(viewMatrix) * vec4(0, 0, 0, 1.0)).xyz - worldPosition.xyz;
}