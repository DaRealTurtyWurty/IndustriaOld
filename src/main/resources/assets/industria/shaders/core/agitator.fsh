#version 150

#moj_import <fog.glsl>

uniform sampler2D Sampler0;
uniform sampler2D Sampler3;
uniform sampler2D Sampler4;

uniform vec4 ColorModulator;
uniform float FogStart;
uniform float FogEnd;
uniform vec4 FogColor;

in float vertexDistance;
in vec4 vertexColor;
in vec2 texCoord0;
in vec4 normal;
in float blendFactor;

out vec4 fragColor;

void main() {
    vec4 color = texture(Sampler0, texCoord0) * vertexColor * ColorModulator;
    fragColor = linear_fog(color, vertexDistance, FogStart, FogEnd, FogColor);

    vec4 blendColor = texture(Sampler3, texCoord0);
    vec4 blendColor2 = texture(Sampler4, texCoord0);
    float blend = (blendColor.r + blendColor.g + blendColor.b) / 3.0;
    float blend2 = (blendColor2.r + blendColor2.g + blendColor2.b) / 3.0;

    fragColor = mix(fragColor, blendColor, blend * blendFactor);
    fragColor = mix(fragColor, blendColor2, blend2 * blendFactor);
}
