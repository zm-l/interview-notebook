# Interview Notebook CLI

A command-line Java application designed to help developers track, manage, and answer technical interview questions. It acts as a local database and uses the Anthropic API (Claude) to automatically generate high-quality, code-backed answers and categorize them by technical topics, ultimately exporting a master Markdown study guide.

## Features
* **Question Tracking:** Add and store interview questions categorized by company.
* **AI Integration:** Automatically fetch deep, technical explanations for unanswered questions using Claude 3.5 Sonnet.
* **Topic Categorization:** The AI automatically tags questions with relevant technical topics (e.g., `Multithreading`, `Design Patterns`).
* **Markdown Export:** Generate a clean, structured `master_notebook.md` study guide to review before interviews.
* **Local Storage:** All data is securely saved to a local `database.json` file.

## Prerequisites
* **Java 17** or higher
* **Apache Maven** (or run via IntelliJ IDEA's built-in Maven)
* An **Anthropic API Key**

## Setup & Configuration

**1. Clone the repository**
```bash
git clone [https://github.com/YourUsername/interview-notebook.git](https://github.com/YourUsername/interview-notebook.git)
cd interview-notebook


**2. Set up your API Key**
The application requires your Anthropic API key to be stored as a system environment variable for security.

**Windows (PowerShell):**

```powershell
$env:ANTHROPIC_API_KEY="sk-ant-api03-..."

```

*(For a permanent setup, add `ANTHROPIC_API_KEY` to your Windows System Environment Variables).*

**Mac/Linux:**

```bash
export ANTHROPIC_API_KEY="sk-ant-api03-..."

```

## Building the Project

Use Maven to compile the code and build an executable "fat JAR" containing all necessary dependencies (Picocli, Jackson, etc.).

```bash
mvn clean package

```

## Usage

Run the compiled JAR file from your terminal.

```bash
java -jar target/interview-notebook-1.0-SNAPSHOT-jar-with-dependencies.jar [OPTIONS]

```

### Command Line Options

| Flag | Long Name | Description |
| --- | --- | --- |
| `-h` | `--help` | Show the help menu and available commands. |
| `-a` | `--add <Company>` | Add a new question under a specific company name. |
| `-q` | `--question <Text>` | The actual interview question text (use with `-a`). |
| `-g` | `--generate` | Scan the database and fetch AI answers for any pending questions. |
| `-m` | `--markdown` | Export the current database into a formatted Markdown file. |
| `-t` | `--topic` | Sort the generated Markdown by technical topic instead of by company. |

### Example Workflow

You can chain commands together. To add a question, immediately generate its answer, and export the updated notebook all in one go:

```bash
java -jar target/interview-notebook-1.0-SNAPSHOT-jar-with-dependencies.jar -a "ByteDance" -q "What is the difference between program stack and heap?" -g -m

```

## Project Structure

* **`Picocli`**: Powers the terminal interface and command parsing.
* **`Jackson`**: Handles seamless JSON reading/writing to the local database.
* **`Java HttpClient`**: Manages native REST calls to the Anthropic API without bloat.
