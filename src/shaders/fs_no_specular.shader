#version 330 core

in vec3 aPos;
in vec3 aNormal;
in vec2 aTexCoord;

out vec4 fragColor;

uniform sampler2D first_texture;
uniform vec3 viewPos;

// Directional Lights
struct DirLight {
    vec3 direction;
    vec3 ambient;
    vec3 diffuse;
    vec3 specular;
};

#define NR_DIR_LIGHTS 1
uniform DirLight dirLights[NR_DIR_LIGHTS];

// Point Lights
struct PointLight {
    vec3 position;
    float constant;
    float linear;
    float quadratic;
    vec3 ambient;
    vec3 diffuse;
    vec3 specular;
};

#define NR_POINT_LIGHTS 1
uniform PointLight pointLights[NR_POINT_LIGHTS];

// Spot Lights
struct Spotlight {
    vec3 position;
    vec3 direction;
    float cutOff;
    float outerCutOff;
    float constant;
    float linear;
    float quadratic;
    vec3 ambient;
    vec3 diffuse;
    vec3 specular;
};

#define NR_SPOT_LIGHTS 1
uniform Spotlight spotLights[NR_SPOT_LIGHTS];

struct Material {
    vec3 ambient;
    vec3 diffuse;
    vec3 specular;
    float shininess;
};

uniform Material material;

vec3 CalcDirLight(DirLight light, vec3 normal, vec3 viewDir);
vec3 CalcPointLight(PointLight light, vec3 normal, vec3 fragPos, vec3 viewDir);
vec3 CalcSpotlight(Spotlight light, vec3 normal, vec3 fragPos, vec3 viewDir);

void main() {
    vec3 norm = normalize(aNormal);
    vec3 viewDir = normalize(viewPos - aPos);

    vec3 result = vec3(0, 0, 0);
    // Directional Lights
    for (int i = 0; i < NR_DIR_LIGHTS; i++)
    result += CalcDirLight(dirLights[i], norm, viewDir);

    // Point lights
    for (int i = 0; i < NR_POINT_LIGHTS; i++)
    result += CalcPointLight(pointLights[i], norm, aPos, viewDir);

    // Spot lights
    for (int i = 0; i < NR_SPOT_LIGHTS; i++)
    result += CalcSpotlight(spotLights[i], norm, aPos, viewDir);

    fragColor = vec4(result, 1.0);
}

// Calculate colour reflected from a directional light
vec3 CalcDirLight(DirLight light, vec3 normal, vec3 viewDir) {
    vec3 lightDir = normalize(-light.direction);
    // diffuse
    float diff = max(dot(normal, lightDir), 0.0);
    //specular
    vec3 reflectDir = reflect(-lightDir, normal);
    float spec = pow(max(dot(viewDir, reflectDir), 0.0), material.shininess);
    // combine results
    vec3 ambient  = light.ambient  * material.ambient * vec3(texture(first_texture, aTexCoord));
    vec3 diffuse  = light.diffuse  * (diff * material.diffuse) * vec3(texture(first_texture, aTexCoord));
    vec3 specular = light.specular * (spec * material.specular);
    return (ambient + diffuse + specular);
}

// Calculate colour reflected from a point light
vec3 CalcPointLight(PointLight light, vec3 normal, vec3 fragPos, vec3 viewDir) {
    vec3 lightDir = normalize(light.position - fragPos);
    // diffuse
    float diff = max(dot(normal, lightDir), 0.0);
    // specular
    vec3 reflectDir = reflect(-lightDir, normal);
    float spec = pow(max(dot(viewDir, reflectDir), 0.0), material.shininess);
    // attenuation
    float distance    = length(light.position - fragPos);
    float attenuation = 1.0 / (light.constant + light.linear * distance +
    light.quadratic * (distance * distance));
    // combine results
    vec3 ambient  = light.ambient  * material.ambient * vec3(texture(first_texture, aTexCoord));
    vec3 diffuse  = light.diffuse  * (diff * material.diffuse) * vec3(texture(first_texture, aTexCoord));
    vec3 specular = light.specular * (spec * material.specular);
    ambient  *= attenuation;
    diffuse  *= attenuation;
    specular *= attenuation;
    return (ambient + diffuse + specular);
}

// Calculate colour reflected from a spot light
vec3 CalcSpotlight(Spotlight light, vec3 normal, vec3 fragPos, vec3 viewDir) {
    vec3 lightDir = normalize(light.position - fragPos);

    // for calculating the radius
    float theta = dot(lightDir, normalize(-light.direction));

    // for gradual change of intensity around edges
    float epsilon   = light.cutOff - light.outerCutOff;
    float intensity = clamp((theta - light.outerCutOff) / epsilon, 0.0, 1.0);

    // add ambient colour both in and outside the spotlight
    vec3 result = light.ambient * vec3(texture(first_texture, aTexCoord));

    // calculate the colour inside the spotlight
    if (theta > light.outerCutOff) {
        float diff = max(dot(normal, lightDir), 0.0);
        vec3 reflectDir = reflect(-lightDir, normal);
        float spec = pow(max(dot(viewDir, reflectDir), 0.0), material.shininess);
        float distance = length(light.position - fragPos);
        float attenuation = 1.0 / (light.constant + light.linear * distance +
        light.quadratic * (distance * distance));


        vec3 diffuse  = light.diffuse  * (diff * material.diffuse) * vec3(texture(first_texture, aTexCoord));
        vec3 specular = light.specular * (spec * material.specular);
        diffuse  *= attenuation;
        specular *= attenuation;

        result += (diffuse * intensity + specular * intensity);
    }

    return result;
}