# 1 "/home/lord448/Documentos/TEC/Tesis/VideojuegoCRITRepo/ESPMCU/FIlterDebugShoulderWheel/FIlterDebugShoulderWheel.ino"
# 2 "/home/lord448/Documentos/TEC/Tesis/VideojuegoCRITRepo/ESPMCU/FIlterDebugShoulderWheel/FIlterDebugShoulderWheel.ino" 2
# 3 "/home/lord448/Documentos/TEC/Tesis/VideojuegoCRITRepo/ESPMCU/FIlterDebugShoulderWheel/FIlterDebugShoulderWheel.ino" 2





typedef enum Axys {
    X,
    Y,
    Z
}Axys;

MPU6050 mpu(Wire); //Object that handles the MPU6050 sensor

Axys workingAxys = X;
Axys pastAxys = X;

void setup()
{
 Serial.begin(115200);
    Wire.begin();
    mpu.begin();
    PhyphoxBLE::start();
    pinMode(33, 0x03);
}

void loop()
{
    static float angle = 0;
 getData(&angle); //Getting data from the sensor
    lowPassFilter(&angle);
    PhyphoxBLE::write(angle);
}

/**

 * @brief Gets the mean value of the lectures (6 Lectures)

 * @note  Since the mean arithmetics needs the absolut scale (0 - 360) 

 *        instead of the relative scale (0 - -180) this functions returns 

 *        the data ready to send

 * @param read: Value where is saved the mean

 */
# 43 "/home/lord448/Documentos/TEC/Tesis/VideojuegoCRITRepo/ESPMCU/FIlterDebugShoulderWheel/FIlterDebugShoulderWheel.ino"
void getData(float *read) {
    const uint32_t numberOfValues = 8;
    float lectures = 0;
    float tmp = 0;
    float mean = 0;
    for(uint32_t i = 0; i < numberOfValues; i++) {
        mpu.update();
        switch (workingAxys) {
            case Y:
                tmp = mpu.getAngleY();
            break;
            case Z:
                tmp = mpu.getAngleZ();
            break;
            case X:
            default:
                tmp = mpu.getAngleX();
            break;
        }
        if(tmp < 0) //Adjust to get absolute scale
            tmp += 360;
        lectures += tmp;
    }
    mean = lectures/numberOfValues;
    mean -= 360;
    *read = mean>0? mean:-mean;
}

void setCoef(float *a, float *b)
{
    const float initSampFreq = 1e3;
    const float sqr2 = sqrt(2);
    static float dt = 0, tn1 = 0;

    float omega = 6.28318530718*(initSampFreq);
    float t = micros()/1.0e6;

    dt = t - tn1;
    tn1 = t;

    float alpha = omega*dt;
    float alphaSq = alpha*alpha;
    float beta[] = {1, sqr2, 1};
    float D = alphaSq*beta[0] + 2*alpha*beta[1] + 4*beta[2];

    b[0] = alphaSq/D;
    b[1] = 2*b[0];
    b[2] = b[0];
    a[0] = -(2*alphaSq*beta[0] - 8*beta[2])/D;
    a[1] = -(beta[0]*alphaSq - 2*beta[1]*alpha + 4*beta[2])/D;
}

/**

 * @brief 2nd order Butterworth low pass filter for the signal processing

 *        of the MPU6050 this filter has a cutoff frequency of 30Hz 

 * 

 * @param angle: Angle that will be processed

 */
# 101 "/home/lord448/Documentos/TEC/Tesis/VideojuegoCRITRepo/ESPMCU/FIlterDebugShoulderWheel/FIlterDebugShoulderWheel.ino"
void lowPassFilter(float *angle)
{
    //Constant coefficients for the filter obtained with the equations on the README files
    static float ACoeff[3] = {1.95558189, -0.95654717, 0};
    static float BCoeff[3] = {0.00024132, 0.00048264, 0.00024132};
    //Variables that interact in the filter difference equation
    static float x[3] = {0, 0, 0};
    static float y[3] = {0, 0, 0};

    setCoef(ACoeff, BCoeff);

    //Input data of the filter
    x[0] = *angle;
    //2nd order Butterworth difference equation
    y[0] = ACoeff[0]*y[1] + ACoeff[1]*y[2] +
           BCoeff[0]*x[0] + BCoeff[1]*x[1] + BCoeff[2]*x[2];
    //Storing data
    for(int i = 1; i >= 0; i--)
    {
        x[i+1] = x[i];
        y[i+1] = y[i];
    }
    //Output of the filter
    *angle = y[0];
}
