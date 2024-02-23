# DBHQ's Happy Bot

Repository for **DBHQ's Happy Bot**.

## Features

- **Cat Pictures**: Brighten your day with adorable and random cat pictures.
- **Scalable Command Structure**:

## Getting Started

Follow these steps to get DBHQ's Happy Bot up and running on your server.

### Prerequisites

- Java JDK 17 or later
- Git installed on your machine

### Installation

1. **Clone the Repository**

   Clone the bot repository to your local machine using Git:

   ```
   git clone https://github.com/sublations/dbhq
   cd dbhq
   ```

2. **Bot Configuration**

   Duplicate the `config.sample.yml` to `config.yml` and fill in the required fields, including your Discord bot token.

   ```
   cp config.sample.yml config.yml
   ```

3. **Build the Project**

   Use Gradle to build the project:

   ```
   ./gradlew build
   ```

4. **Run the Bot**

   Start your bot using:

   ```
   java -jar build/libs/dbhq-happy-bot-all.jar
   ```

## Secure Configuration with SOPS

To ensure sensitive configuration details such as API keys and tokens are securely managed, we use SOPS (Secrets OPerationS) for encryption and decryption of our `config.yml` file.

### Encrypting Your Config

1. **Install SOPS**: Ensure SOPS is installed on your system. For installation instructions, see the [SOPS GitHub repository](https://github.com/getsops/sops).

2. **Encrypt `config.yml`**: Replace `<YOUR_PGP_FINGERPRINT>` with your PGP key fingerprint.
   ```
   sops --encrypt --pgp <YOUR_PGP_FINGERPRINT> config.yml > config.enc.yml
   ```

3. **Commit the Encrypted File**: Commit `config.enc.yml` to your repository. Do not commit the plaintext `config.yml`.
   ```
   git add config.enc.yml
   git commit -m "Add encrypted config file"
   ```

### Decrypting the Config Locally

To decrypt the config file for local development or deployment, use:
```
sops --decrypt config.enc.yml > config.yml
```

### Automated Decryption

For automated environments (CI/CD), ensure that the PGP private key used for decryption is securely stored and accessible by your automation system.


## Architecture

This bot is designed with modularity and scalability in mind. The architecture is centered around a command handling system that makes it simple to add new features.

- **Main**: Initializes the bot and sets up the command handling.
- **CommandManager**: Manages the registration and execution of commands.
- **Commands**: Individual implementations of the `ICommand` interface, representing actions the bot can perform.
- **ConfigManager**: Handles configuration loading from `config.yml`, ensuring sensitive information is kept secure. (Encrypted using SOPs)

## Adding Commands

To contribute a new command:

1. Implement the `ICommand` interface in a new class under `src/main/java/dbhq/bot/command/commands`.
2. Register this new command in the `CommandManager` to make it available for use.

## License

DBHQ's Happy Bot is released under the MIT License. See the `LICENSE` file for more details.

## Acknowledgments

- Thanks to the Javacord library for the Discord API wrapper.
- fwds for being cool

