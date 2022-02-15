
static struct PidGain
{
public:
  double kp;
  double ki;
  double kd;

  PidGain(double _kp, double _ki, double _kd) : kp(_kp), ki(_ki), kd(_kd) {}
};

int g_Value;

enum Enum
{
    Black,
    White
};
//aaaaa
union Union
{
    int i;
    char ch;
};

void func()
{
    g_Value + 2;
}

class Pid
{
public:
  Pid(double _kp, double _ki, double _kd, double _targetValue)
      : gain(_kp, _ki, _kd), preDeviation(0.0), integral(0.0), targetValue(_targetValue)
  {
  }

  ~Pid(double _kp, double _ki, double _kd, double _targetValue)
    {
    }

  void setPidGain(double _kp, double _ki, double _kd)
  {
    gain.kp = _kp;
    gain.ki = _ki;
    gain.kd = _kd;
  }

  double calculatePid(double presentValue, double delta)
  {
    if (delta == 0)
      delta = 0.01;
    double presentDeviation = targetValue - presentValue;
    integral += presentDeviation * delta;
    double difference = (presentDeviation - preDeviation) / delta;
    preDeviation = presentDeviation;

    double p = gain.kp * presentDeviation;
    double i = gain.ki * integral;
    double d = gain.kd * difference;

    return (p + i + d);
  }

private:
class sample2
    {
    private:
        int c;
        int d;
    };

enum Enum
{
    Black,
    White
};

  PidGain gain;
  double preDeviation;
  static double integral;
  double targetValue = 1;
};