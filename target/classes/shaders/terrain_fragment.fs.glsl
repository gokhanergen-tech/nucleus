#version 400
const int MAX_POINT_LIGHTS=5;
const int MAX_SPOT_LIGHTS=5;

in vec2 fragTextureCoord;
in vec3 fragNormal;
in vec3 fragPos;

out vec4 fragColor;

struct Material {
    vec4 ambient;
    vec4 diffuse;
    vec4 specular;

    bool hasTexture;

    float reflectance;
};

struct DirectionalLight{
    float intensity;
    vec3  colour;
    vec3  direction;
};

struct PointLight{
    vec3 colour;
    vec3 position;
    float linear;
    float constant;
    float intensity;
    float exponent;
};

struct SpotLight{
    vec3 coneDirection;
    float cutoff;
    PointLight pointLight;
};

uniform sampler2D backgroundTexture;
uniform sampler2D redTexture;
uniform sampler2D greenTexture;
uniform sampler2D blueTexture;
uniform sampler2D blendMap;

uniform vec3 ambientLight;
uniform Material material;
uniform DirectionalLight directionalLight;
uniform float specularPower;
uniform PointLight pointLights[MAX_POINT_LIGHTS];
uniform SpotLight spotLights[MAX_SPOT_LIGHTS];

vec4 ambientC;
vec4 diffuseC;
vec4 specularC;

void setupColors(Material material,vec2 fragTextureCoord){
    if (material.hasTexture == false){
        vec4 blendMapColour=texture(blendMap,fragTextureCoord);
        float backgroundTextureAmout=1-(blendMapColour.r+blendMapColour.g+blendMapColour.b);
        vec2 tiledCoords=fragTextureCoord*1000;
        vec4 backgroundTextureColour=texture(backgroundTexture,tiledCoords)*backgroundTextureAmout;
        vec4 greenTextureColour=texture(greenTexture,tiledCoords)*blendMapColour.g;
        vec4 redTextureColour=texture(redTexture,tiledCoords)*blendMapColour.r;
        vec4 blueTextureColour=texture(blueTexture,tiledCoords)*blendMapColour.b;

        ambientC = backgroundTextureColour+redTextureColour+greenTextureColour+blueTextureColour;
        diffuseC = ambientC;
        specularC = ambientC;
    }
    else
    {
        ambientC = material.ambient;
        diffuseC = material.diffuse;
        specularC = material.specular;
    }


}

vec4 calcLightColour(vec3 light_colour,float light_intensity,vec3 position,vec3 to_light_dir,vec3 normal){
    vec4 diffuseColor=vec4(0,0,0,0);
    vec4 specularColor=vec4(0,0,0,0);

    //diffuse light
    float diffuseFactor=max(dot(normal,to_light_dir),0.0);
    diffuseColor=diffuseC*vec4(light_colour,1.0)*light_intensity*diffuseFactor;

    //specular color
    vec3 camera_direction=normalize(-position);
    vec3 from_light_dir=-to_light_dir;
    vec3 reflectedLight=normalize(reflect(from_light_dir,normal));
    float specularFactor=max(dot(camera_direction,reflectedLight),0.0);
    specularFactor=pow(specularFactor,specularPower);
    specularColor=specularC*light_intensity*specularFactor*material.reflectance*vec4(light_colour,1.0);

    return (specularColor+diffuseColor);
}

vec4 calcDirectionalLight(DirectionalLight directionalLight,vec3 position,vec3 normal){
    return calcLightColour(directionalLight.colour,directionalLight.intensity,position,normalize(directionalLight.direction),normal);
}

vec4 calcPointLight(PointLight pointLight,vec3 position,vec3 normal){
    vec3 light_dir=pointLight.position-position;
    vec3 to_light_dir=normalize(light_dir);
    vec4 light_colour=calcLightColour(pointLight.colour,pointLight.intensity,position,to_light_dir,normal);


    //attenuation
    float distance=length(light_dir);
    float attenuationInv=pointLight.constant+pointLight.linear*distance+pointLight.exponent*distance*distance;
    return light_colour/attenuationInv;

}

vec4 calcSpotLight(SpotLight spotLight,vec3 position,vec3 normal){
    vec3 light_dir=spotLight.pointLight.position;
    vec3 to_light_dir=normalize(light_dir);
    vec3 from_light_dir=-to_light_dir;
    float spot_alfa=dot(from_light_dir,normalize(spotLight.coneDirection));

    vec4 colour=vec4(0,0,0,0);

    if(spot_alfa>spotLight.cutoff){
        colour=calcPointLight(spotLight.pointLight,position,normal);
        colour*=(1.0-(1.0-spot_alfa)/(1.0-spotLight.cutoff));
    }
    return colour;
}

void main() {
    setupColors(material,fragTextureCoord);


    vec4 diffuseSpecularComp=calcDirectionalLight(directionalLight,fragPos,fragNormal);
    //diffuseSpecularComp=vec4(0,0,0,0);
    for(int i=0;i<MAX_POINT_LIGHTS;i++){
        if(pointLights[i].intensity>0)
        diffuseSpecularComp+=calcPointLight(pointLights[i],fragPos,fragNormal);
    }
    for(int i=0;i<MAX_SPOT_LIGHTS;i++){
        if(spotLights[i].pointLight.intensity>0)
        diffuseSpecularComp+=calcSpotLight(spotLights[i],fragPos,fragNormal);
    }



    fragColor=ambientC*vec4(ambientLight,1)+diffuseSpecularComp;
}
