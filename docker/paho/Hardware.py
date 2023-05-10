class Hardware:
    def __init__(self, id_hardware, hardware_name, hardware_address):
        self.id_hardware = id_hardware
        self.hardware_name = hardware_name
        self.hardware_address = hardware_address

    def to_dict(self):
        return {
            "id_hardware": self.id_hardware,
            "hardware_name": self.hardware_name,
            "hardware_address": self.hardware_address
        }