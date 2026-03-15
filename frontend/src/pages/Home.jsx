import { Link } from "react-router-dom";
import '../styles/Home.css';


function Home() {
    return (
        <main className="home">
            <section className="home-hero">
                <p className="home-kicker">JustOrder</p>
                <h1>Order food from your favourite restaurants</h1>
                <div className="home-actions">
                    <Link className="btn btn-primary" to="/register-customer">
                        Create customer account
                    </Link>
                    <Link className="btn btn-primary" to="/register-restaurant">
                        Create restaurant account
                    </Link>
                    <Link className="btn btn-primary" to="/register-rider">
                        Create rider account
                    </Link>
                    <a className="btn btn-secondary" href="http://localhost:8080/api/hello" target="_blank" rel="noreferrer">
                        Test API
                    </a>
                </div>
            </section>
        </main>
    );
}

export default Home;