#version 130

uniform sampler2D tex;
uniform float time;
uniform float alpha;


// The MIT License
// Copyright © 2017 Inigo Quilez
// Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions: The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software. THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.


// Computes the analytic derivatives of a 3D Value Noise. This can be used for example to compute normals to a
// 3d rocks based on Value Noise without approximating the gradient by haveing to take central differences (see
// this shader: https://www.shadertoy.com/view/XttSz2)


// Value    Noise 2D, Derivatives: https://www.shadertoy.com/view/4dXBRH
// Gradient Noise 2D, Derivatives: https://www.shadertoy.com/view/XdXBRH
// Value    Noise 3D, Derivatives: https://www.shadertoy.com/view/XsXfRH
// Gradient Noise 3D, Derivatives: https://www.shadertoy.com/view/4dffRH
// Value    Noise 2D             : https://www.shadertoy.com/view/lsf3WH
// Value    Noise 3D             : https://www.shadertoy.com/view/4sfGzS
// Gradient Noise 2D             : https://www.shadertoy.com/view/XdXGW8
// Gradient Noise 3D             : https://www.shadertoy.com/view/Xsl3Dl
// Simplex  Noise 2D             : https://www.shadertoy.com/view/Msf3WH
// Wave     Noise 2D             : https://www.shadertoy.com/view/tldSRj


float hash(vec3 p)// replace this by something better
{
    p  = 50.0*fract(p*0.3183099 + vec3(0.71, 0.113, 0.419));
    return -1.0+2.0*fract(p.x*p.y*p.z*(p.x+p.y+p.z));
}


// return value noise (in x) and its derivatives (in yzw)
vec4 noised(in vec3 x)
{
    vec3 i = floor(x);
    vec3 w = fract(x);

    /*// cubic interpolation used because it doesn't rush to extreme values too fast
    #if 0
        // quintic interpolation
        vec3 u = w*w*w*(w*(w*6.0-15.0)+10.0);
        vec3 du = 30.0*w*w*(w*(w-2.0)+1.0);
    #else
        // cubic interpolation
        vec3 u = w*w*(3.0-2.0*w);
        vec3 du = 6.0*w*(1.0-w);
    #endif*/

    // MODIFIED!
    // Cosine interpolation seems to produce the fewest artifacts actually
    vec3 u = 0.5-0.5*cos(3.1416 * w);
    vec3 du = 0.5*3.1416*sin(3.1416 * w);


    float a = hash(i+vec3(0.0, 0.0, 0.0));
    float b = hash(i+vec3(1.0, 0.0, 0.0));
    float c = hash(i+vec3(0.0, 1.0, 0.0));
    float d = hash(i+vec3(1.0, 1.0, 0.0));
    float e = hash(i+vec3(0.0, 0.0, 1.0));
    float f = hash(i+vec3(1.0, 0.0, 1.0));
    float g = hash(i+vec3(0.0, 1.0, 1.0));
    float h = hash(i+vec3(1.0, 1.0, 1.0));

    float k0 =   a;
    float k1 =   b - a;
    float k2 =   c - a;
    float k3 =   e - a;
    float k4 =   a - b - c + d;
    float k5 =   a - c - e + g;
    float k6 =   a - b - e + f;
    float k7 = - a + b + c - d + e - f - g + h;

    return vec4(k0 + k1*u.x + k2*u.y + k3*u.z + k4*u.x*u.y + k5*u.y*u.z + k6*u.z*u.x + k7*u.x*u.y*u.z,
    du * vec3(k1 + k4*u.y + k6*u.z + k7*u.y*u.z,
    k2 + k5*u.z + k4*u.x + k7*u.z*u.x,
    k3 + k6*u.x + k5*u.y + k7*u.x*u.y));
}

// end IQ Value Noise code.
// The following domain rotation and caustics code is released under CC0
// https://creativecommons.org/share-your-work/public-domain/cc0/

vec4 noised_improveXYPlanes(in vec3 x)
{
    mat3 orthonormalMap = mat3(
    0.788675134594813, -0.211324865405187, -0.577350269189626,
    -0.211324865405187, 0.788675134594813, -0.577350269189626,
    0.577350269189626, 0.577350269189626, 0.577350269189626);
    x = x * orthonormalMap;

    vec4 result = noised(x);
    result.yzw = orthonormalMap * result.yzw;
    return result;
}

//Better to do the domain warping in the rotated space so we don't have to unrotate and re-rotate the derivative vector
float noised_caustics_improveXYPlanes(in vec3 x)
{
    mat3 orthonormalMap = mat3(
    0.788675134594813, -0.211324865405187, -0.577350269189626,
    -0.211324865405187, 0.788675134594813, -0.577350269189626,
    0.577350269189626, 0.577350269189626, 0.577350269189626);
    x = x * orthonormalMap;

    vec4 result = noised(x);
    float value = noised(x - 0.125 * result.yzw).x;

    return value;
}


void main()
{
    vec2 coord = gl_TexCoord[0].xy;
    vec4 color = texture2D(tex, coord);
    vec2 texSize = vec2(textureSize(tex, 0));
    // Width * 2
    vec2 factor = normalize(texSize) * vec2(2.0, 1.0);
    // Normalized pixel coordinates (from 0 to 1)
    vec2 uv = coord * factor;

    float noiseX = noised_caustics_improveXYPlanes(vec3(uv * 12, time * 1.5));
    float noiseY = noised_caustics_improveXYPlanes(vec3(uv * 16, time * 2.5));

    noiseX = noiseX * 0.8 + 0.2;

    vec3 resultC = noiseX * mix(vec3(.431, .8, 1.0), vec3(.2, 0.1, 0.8), noiseY);

    float totalAlpha = 0.0;
    float actualAlpha = 0.0;

    float blurRadius = 10.0;
    for (float x = -blurRadius; x <= blurRadius; x += 1.0) {
        for (float y = -blurRadius; y <= blurRadius; y += 1.0) {
            float weight = (blurRadius - abs(x)) * (blurRadius - abs(y));
            vec2 offset = vec2(x, y) / texSize;// Convert pixel offset to texcoord
            totalAlpha += weight;
            actualAlpha += texture2D(tex, coord + offset).a * weight;
        }
    }

    float blurAlphaMult = actualAlpha / totalAlpha;

    float blurThreshold = 0.8;
    if (blurAlphaMult > 0.0) {
        gl_FragColor = vec4(resultC, alpha * blurAlphaMult / blurThreshold);
    } else {
        gl_FragColor = vec4(0);
    }
}
