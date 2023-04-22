#version 400 core

in vec3 position;
in vec2 texCoord;
in vec3 normal;

//out vec3 colour;
out vec2 fragTextureCoord;
out vec3 fragNormal;
out vec3 fragPos;

uniform mat4 transformationMatrix;
uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;

void main(){
    vec4 worldPos=transformationMatrix*vec4(position,1.0);
    gl_Position=projectionMatrix*viewMatrix*worldPos;

    fragNormal=normalize(worldPos).xyz;
    fragPos=worldPos.xyz;
    fragTextureCoord=texCoord;
    //colour=vec3(position.x+0.25,0.17,position.y+0.25);

}
