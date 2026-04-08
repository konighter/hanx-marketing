# Reddit Automated Marketing Microservice Design Specification

## 1. Overview
This microservice aims to provide a reliable, scalable, and human-like automated marketing solution on Reddit. It focuses on two core pillars: **Content Distribution** and **Lead Generation** using a multi-persona approach to blend into communities naturally.

## 2. Goals
- **Lead Generation**: Identify high-intent users in specific subreddits who are discussing problems that our product can solve.
- **Content Distribution**: Automatically post or reply to threads using diverse "Expert Persona" accounts to build brand authority and drive traffic.
- **Risk Mitigation**: Avoid shadowbans and detection by simulating human behavior (proxies, schedules, and AI-generated variations).

## 3. Architecture
The system will be built as a **Standalone Microservice** to ensure isolation from the main ERP system. 

### 3.1 Components
- **Account & Persona Manager**: Logic to manage multiple Reddit accounts, each tied to a unique persona.
- **Independent Database (PostgreSQL)**: A dedicated database instance to store all service-specific data (accounts, personas, leads, history), ensuring strict isolation from the `hzapp` core DB.
- **Task Queue (Redis)**: Manages asynchronous job distribution for scraping, post-generation, and execution to ensure reliability and scalability.
- **Subreddit Scraper (PRAW-based)**: Real-time listener for keywords and phrases across target subreddits.
- **AI Intent Engine (via hanx-ai)**: Analyzes threads to determine if a post warrants an action (Is it a question? Is it a complaint? What is the user's sentiment?).
- **Contextual Content Generator**: Generates persona-specific replies. It uses the thread content, subreddit rules, and persona profile as context.
- **Task Scheduler & Proxy Manager**: Manages the timing of posts and rotates IPs (Residential Proxies) to prevent account linking.

## 4. User Personas (Scenario B)
Instead of a single brand voice, the system maintains a pool of "Expert Users":
- **The Helpful Consultant**: Provides technical, long-form value first, subtle product mention second.
- **The Enthusiast**: Shared personal experiences ("I've been using this tool for months and it saved me X hours").
- **The Skeptic-turned-Believer**: "I used to hate X-type products, but [Product] actually works differently."

## 5. Workflow
1. **Discovery**: Scraper identifies a potential lead in `r/SaaS`.
2. **Analysis**: Intent Engine confirms the user is looking for an ERP solution.
3. **Selection**: System selects a "Helpful Consultant" persona account that is "awake" and hasn't posted recently.
4. **Generation**: AI generates a reply that answers the user's specific technical question and mentions the product as a resource.
5. **Execution**: Scheduler waits 5-15 minutes (randomized) before posting via a dedicated proxy.
6. **Tracking**: System monitors if the reply gets upvotes or replies.

## 6. Security & Anti-Detection
- **Residential Proxies**: Mandatory to avoid being flagged as a data center bot.
- **Browser Fingerprinting**: Use automation libraries like Playwright with stealth plugins for sensitive actions (e.g., initial login).
- **Human-in-the-loop (Optional)**: A review queue for high-value leads before the bot posts.

## 7. Tech Stack
- **Languages**: Python (FastAPI) for PRAW and AI integration.
- **Databases**: 
  - **PostgreSQL**: Dedicated instance for persistent storage.
  - **Redis**: For task queuing and rate-limit state management.
- **AI Logic**: Integration with existing `hzapp-module-hanx-ai`.
- **Environment**: Containerized (Docker) for easy deployment.
