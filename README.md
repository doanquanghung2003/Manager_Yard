# ManagerYard - Football Yard Management System

## Overview
ManagerYard is a JavaFX-based desktop application for managing football yard bookings and services. The application provides both client and manager interfaces for booking football yards, managing services, and handling payments.

## Features

### Client Features
- **Yard Browsing**: View available football yards with details
- **Booking System**: Book time slots for football yards
- **Service Management**: Add additional services to bookings
- **Payment Processing**: Handle deposits and full payments
- **User Authentication**: Login and registration system
- **Booking History**: View past and current bookings

### Manager Features
- **Yard Management**: Add, edit, and manage football yards
- **Booking Overview**: View and manage all bookings
- **Service Management**: Add and manage additional services
- **Time Slot Management**: Configure available time slots
- **Revenue Tracking**: Monitor booking revenue and statistics

## Project Structure

```
ManagerYard/
├── src/
│   ├── application/
│   │   ├── Main.java              # Application entry point
│   │   └── application.css        # Global styles
│   ├── bean/                      # Data models
│   │   ├── YardModel.java         # Football yard data model
│   │   ├── BookingModel.java      # Booking data model
│   │   ├── UserModel.java         # User data model
│   │   ├── ServicesModel.java     # Service data model
│   │   ├── TimeSlot.java          # Time slot data model
│   │   ├── PaymentStatus.java     # Payment status enum
│   │   ├── YardType.java          # Yard type enum
│   │   └── Bean.java              # Base interface for models
│   ├── controller/                # JavaFX controllers
│   │   ├── BaseController.java    # Base controller interface
│   │   ├── YardController.java    # Yard management controller
│   │   ├── BookingController.java # Booking management controller
│   │   ├── LoginController.java   # Authentication controller
│   │   ├── RegisterController.java # Registration controller
│   │   └── ServiceController.java # Service management controller
│   ├── fxmlclient/               # Client UI layouts
│   │   ├── ProductsLayout.fxml    # Main yard browsing interface
│   │   ├── Login.fxml            # Login interface
│   │   ├── Register.fxml         # Registration interface
│   │   ├── Checkout.fxml         # Booking checkout interface
│   │   └── DetailProduct.fxml    # Yard detail interface
│   ├── fxmlManager/              # Manager UI layouts
│   │   ├── ManagerYardLayout.fxml # Yard management interface
│   │   └── YardMap.fxml          # Yard map interface
│   ├── singleton/
│   │   └── DuLieu.java           # Data singleton for file operations
│   └── utils/
│       ├── JsonUtils.java         # JSON utility functions
│       ├── ImageUtil.java         # Image handling utilities
│       └── QRUtill.java          # QR code utilities
├── bin/                          # Compiled classes
├── data/yards.json                    # Yard data storage
├── data/bookings.json                 # Booking data storage
├── data/services.json                 # Service data storage
└── build.fxbuild                 # JavaFX build configuration
```

## Data Models

### YardModel
Represents a football yard with properties:
- `yardId`: Unique identifier
- `yardName`: Name of the yard
- `yardType`: Type (FIVE_A_SIDE, SEVEN_A_SIDE, etc.)
- `yardPrice`: Price per time slot
- `yardDescription`: Description
- `yardAddress`: Physical address
- `yardStatus`: Availability status
- `isAvailable`: Boolean availability flag
- `maintenanceSlots`: Maintenance time slots
- `eventSlots`: Event time slots

### BookingModel
Represents a booking with properties:
- `bookingId`: Unique identifier
- `bookerName`: Customer name
- `bookerPhone`: Customer phone
- `bookingTime`: Booking timestamp
- `depositAmount`: Deposit amount
- `totalAmount`: Total booking amount
- `paymentStatus`: Payment status
- `serviceIds`: List of service IDs
- `bookingYards`: List of booked yards and slots

### UserModel
Represents a user account with properties:
- `userId`: Unique identifier
- `username`: Username
- `password`: Password (should be hashed)
- `email`: Email address
- `phone`: Phone number
- `role`: User role (CLIENT/MANAGER)

## Key Features

### Data Persistence
- Uses JSON files for data storage
- Automatic file loading and saving
- Singleton pattern for data management

### UI/UX Design
- Modern JavaFX interface
- Responsive design with CSS styling
- Consistent color scheme and typography
- Intuitive navigation

### Booking System
- Time slot selection
- Service add-ons
- Payment processing
- Booking confirmation

## Setup and Installation

### Prerequisites
- Java 11or higher
- JavaFX SDK
- Eclipse or IntelliJ IDEA (recommended)

### Build Configuration
The project uses `build.fxbuild` for JavaFX application packaging:

```xml
<?xml version="1.0 encoding="ASCII"?>
<anttasks:AntTask xmi:version="2.0xmlns:xmi="http://www.omg.org/XMI xmlns:anttasks="http://org.eclipse.fx.ide.jdt/10buildDirectory="$[object Object]project}/build">
  <deploy>
    <application name="ManagerYard"/>
    <info/>
  </deploy>
  <signjar/>
</anttasks:AntTask>
```

### Running the Application
1. Ensure JavaFX is properly configured
2Run `Main.java` as a JavaFX application
3e application will start with the client interface

## Data Files

### data/yards.json
Stores yard information in JSON format:
```json
  [object Object]   yardId":uuid,
   yardName": Yard Name,
   yardType:FIVE_A_SIDE",
  yardPrice: 10  yardDescription:Description",
  yardAddress": "Address,
 yardStatus:active,
isAvailable": true
  }
]
```

### data/bookings.json
Stores booking information:
```json
  [object Object]    bookingId": "uuid",
   bookerName":Customer Name",
    bookerPhone": "Phone",
  bookingTime:225011:00",
   depositAmount": 50,
    totalAmount: 10000    paymentStatus": "PAID",
 bookingYards": [...]
  }
]
```

### data/services.json
Stores service information:
```json
  [object Object]    serviceId": "uuid",
  serviceName": "Service Name",
  description:Description",
    price": 200``

## Development Guidelines

### Code Organization
- Follow MVC pattern with JavaFX
- Use singleton for data management
- Implement proper error handling
- Use consistent naming conventions

### UI Guidelines
- Use CSS for styling
- Implement responsive design
- Follow accessibility guidelines
- Maintain consistent color scheme

### Data Management
- Use JSON for data persistence
- Implement proper validation
- Handle file I/O exceptions
- Maintain data integrity

## Known Issues and Improvements

### Current Issues
1. **Image Paths**: Some image paths in FXML files are hardcoded to local paths
2. **Error Handling**: Limited error handling in some controllers
3alidation**: Input validation could be improved
4. **Security**: Password storage should use proper hashing

### Recommended Improvements
1. **Database Integration**: Replace JSON files with proper database
2. **Image Management**: Implement proper image handling system
3. **Logging**: Add comprehensive logging system
4. **Testing**: Implement unit and integration tests
5**Documentation**: Add JavaDoc comments to all classes

## Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Test thoroughly
5. Submit a pull request

## License

This project is licensed under the MIT License - see the LICENSE file for details.

## Support

For support and questions, please contact the development team or create an issue in the repository. 