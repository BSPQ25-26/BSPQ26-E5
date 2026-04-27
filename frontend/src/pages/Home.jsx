import React, { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import { loginUser } from "../api/authApi"; 
import sushiImage from "../assets/images/delicioso-rollo-sushi-california-aguacate-anguila-pepino_1125744-1587.jpg";

function Home() {
    const [isLoginOpen, setIsLoginOpen] = useState(false);
    const [isSignUpOpen, setIsSignUpOpen] = useState(false);
    const [loginType, setLoginType] = useState("customer");
    
    const [identifier, setIdentifier] = useState("");
    const [password, setPassword] = useState("");
    
    const navigate = useNavigate();

    const handleLogin = async (e) => {
    e.preventDefault();

    const trimmedIdentifier = identifier.trim();
    const payload = { email: trimmedIdentifier, password };

    try {
        const result = await loginUser(loginType, payload);

        if (result.isBypass) {
            alert("Backend endpoint is NOT IMPLEMENTED yet. Bypassing for UI testing.");
        }

        localStorage.setItem("token", result.token);

        // Persist the logged-in user so the rest of the app can auto-fill things
        if (result.user) {
            localStorage.setItem("userType", loginType);
            localStorage.setItem("user", JSON.stringify(result.user));
        }

        if (loginType === "customer") navigate("/customer-marketplace");
        else if (loginType === "rider") navigate("/rider-dashboard");
        else alert("Restaurant dashboard coming soon!");

    } catch (error) {
        if (error.message === "Failed to fetch" || error.message.includes("NetworkError")) {
            alert("Could not connect to the server. Is Spring Boot running?");
        } else {
            alert(error.message);
        }
    }
};

    const handleTabSwitch = (type) => {
        setLoginType(type);
        setIdentifier("");
        setPassword("");
    };

    return (
        <main className="home-page">
            <section className="home-shell">
                <header className="home-navbar">
                    <div className="brand-group" aria-label="JustOrder home">
                        <h1 className="brand-title">JustOrder</h1>
                    </div>

                    <div className="home-header-right">
                        <nav className="home-nav-links" aria-label="Main navigation">
                            <Link to="/register-restaurant" className="nav-link">
                                Register your restaurant
                            </Link>
                            
                            <button 
                                className="nav-link nav-link-button" 
                                type="button" 
                                onClick={() => {
                                    setIsLoginOpen(!isLoginOpen);
                                    setIsSignUpOpen(false);
                                }}
                            >
                                Log in
                            </button>

                            <button
                                className="nav-link nav-link-button signup-button"
                                type="button"
                                onClick={() => {
                                    setIsSignUpOpen(!isSignUpOpen);
                                    setIsLoginOpen(false);
                                }}
                            >
                                Sign up
                            </button>

                            {isSignUpOpen && (
                                <div className="home-dropdown home-signup-dropdown">
                                    <Link
                                        to="/register-customer"
                                        className="dropdown-link"
                                        onClick={() => setIsSignUpOpen(false)}
                                    >
                                        Sign up as customer
                                    </Link>
                                    <Link
                                        to="/register-rider"
                                        className="dropdown-link"
                                        onClick={() => setIsSignUpOpen(false)}
                                    >
                                        Sign up as rider
                                    </Link>
                                </div>
                            )}
                            

                            {isLoginOpen && (
                                <div className="home-dropdown home-login-dropdown">
                                    <div className="login-tabs">
                                        <button 
                                            onClick={() => handleTabSwitch('customer')}
                                            className={`login-tab ${loginType === 'customer' ? 'active' : ''}`}
                                        >Customer</button>
                                        <button 
                                            onClick={() => handleTabSwitch('rider')}
                                            className={`login-tab ${loginType === 'rider' ? 'active' : ''}`}
                                        >Rider</button>
                                        <button 
                                            onClick={() => handleTabSwitch('restaurant')}
                                            className={`login-tab ${loginType === 'restaurant' ? 'active' : ''}`}
                                        >Restaurant</button>
                                    </div>

                                    <form onSubmit={handleLogin} className="login-form">
                                        <input 
                                            type="email" 
                                            placeholder="Enter your Email"
                                            value={identifier}
                                            onChange={(e) => setIdentifier(e.target.value)}
                                            required
                                            className="login-input"
                                        />
                                        <input 
                                            type="password" 
                                            placeholder="Password" 
                                            value={password}
                                            onChange={(e) => setPassword(e.target.value)}
                                            required
                                            className="login-input"
                                        />
                                        <button 
                                            type="submit"
                                            className="login-submit"
                                        >
                                            Log In
                                        </button>
                                    </form>
                                </div>
                            )}

                        </nav>
                    </div>
                </header>

                <section className="hero-panel">
                    <div className="hero-image-wrap">
                        <img src={sushiImage} alt="Sushi roll on a plate" className="hero-image" />
                    </div>

                    <div className="hero-copy">
                        <h2>
                            DELIVERING HAPPINESS
                            <span>TO YOUR</span>
                            <strong>DOOR</strong>
                        </h2>
                    </div>
                </section>

                <section className="featured-section" aria-labelledby="top-restaurants-heading">
                    <div className="featured-content" />
                </section>
            </section>
        </main>
    );
}

export default Home;