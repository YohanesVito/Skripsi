class Logging:
    def __init__(self, id_logging=None, id_hardware=None, id_user=None, timestamp=None,
                 speed=None, rpm=None, battery=None, lat=None, lon=None, compass=None,
                 duty_cycle=None):
        self.id_logging = id_logging
        self.id_hardware = id_hardware
        self.id_user = id_user
        self.timestamp = timestamp
        self.speed = speed
        self.rpm = rpm
        self.battery = battery
        self.lat = lat
        self.lon = lon
        self.compass = compass
        self.duty_cycle = duty_cycle

    def to_dict(self):
        return {
            "idLogging": self.id_logging,
            "idHardware": self.id_hardware,
            "idUser": self.id_user,
            "timeStamp": self.timestamp,
            "speed": self.speed,
            "rpm": self.rpm,
            "battery": self.battery,
            "lat": self.lat,
            "lon": self.lon,
            "compass": self.compass,
            "dutyCycle": self.duty_cycle
        }
