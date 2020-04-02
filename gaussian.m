A = 1;
x0 = 0; y0 = 0;

sigma_X = 1;
sigma_Y = 2;

[X, Y] = meshgrid(-5:.1:5, -5:.1:5);

for theta = 0:pi/100:pi
    a = cos(theta)^2/(2*sigma_X^2) + sin(theta)^2/(2*sigma_Y^2);
    b = -sin(2*theta)/(4*sigma_X^2) + sin(2*theta)/(4*sigma_Y^2);
    c = sin(theta)^2/(2*sigma_X^2) + cos(theta)^2/(2*sigma_Y^2);

    Z = A*exp( - (a*(X-x0).^2 + 2*b*(X-x0).*(Y-y0) + c*(Y-y0).^2));

surf(X,Y,Z);shading interp;view(-36,36)
waitforbuttonpress
end
