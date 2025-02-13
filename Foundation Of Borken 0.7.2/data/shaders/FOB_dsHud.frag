// Made by k-mouse (2016-11-23)
// Modified from David Hoskins (2013-07-07) and joltz0r (2013-07-04)
#define TAU 6.28318530718

#define TILING_FACTOR 1
#define MAX_ITER 8

uniform vec2 screen;
uniform float iTime;
uniform float alpha;

float waterHighlight(vec2 p, float time, float foaminess)
{
    vec2 i = vec2(p);
    float c = 0.0;
    float foaminess_factor = mix(1.0, 6.0, foaminess);
    float inten = .005 * foaminess_factor;

    for (int n = 0; n < MAX_ITER; n++)
    {
        float t = time * (1.0 - (3.5 / float(n+1)));
        i = p + vec2(cos(t - i.x) + sin(t + i.y), sin(t - i.y) + cos(t + i.x));
        c += 1.0/length(vec2(p.x / (sin(i.x+t)), p.y / (cos(i.y+t))));
    }
    c = 0.2 + c / (inten * float(MAX_ITER));
    c = 1.17-pow(c, 1.4);
    c = pow(abs(c), 8.0);
    return c / sqrt(foaminess_factor);
}

void main()
{
    float time = iTime * 0.1+23.0;
    vec2 uv = gl_FragCoord.xy / screen.xy;
    vec2 uv_square = vec2(uv.x * screen.x / screen.y, uv.y);
    float dist_center = pow(2.0*length(uv - 0.5), 2.0);

    float foaminess = smoothstep(0.4, 1.8, dist_center);
    float clearness = 0.1 + 0.9*smoothstep(0.1, 0.5, dist_center);

    vec2 p = mod(uv_square*TAU*TILING_FACTOR, TAU)-150.0;

    float c = waterHighlight(p, time, foaminess);

    vec3 water_color = vec3(0, 0.6, 0.7);
    vec3 color = water_color * c;
    //    color = clamp(color + water_color, 0.0, 1.0);
    //
    //    color = mix(water_color, color, clearness);
    gl_FragColor = vec4(color, alpha);
}