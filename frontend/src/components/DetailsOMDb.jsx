import React, { useEffect, useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { Container, Card, Spinner, Button } from 'react-bootstrap';


export default function DetailsOMDb() {
  const API_KEY = import.meta.env.VITE_API_KEY;
  const API_URL = import.meta.env.VITE_API_URL;
  const { imdbId } = useParams();
  const [details, setDetails] = useState(null);
  const [loading, setLoading] = useState(true);
  const navigation = useNavigate();

  const handleBack = () => {
    navigation(-1);
  }

  useEffect(() => {
    const fetchDetails = async () => {
      setLoading(true);
      const res = await fetch(`${API_URL}?apikey=${API_KEY}&i=${imdbId}&plot=full`);
      const data = await res.json();
      setDetails(data);
      setLoading(false);
    };
    fetchDetails();
  }, [imdbId]);

  if (loading) {
    return (
      <Container className="text-center text-light my-5">
        <Spinner animation="border" variant="light" />
        <p>Carregando...</p>
      </Container>
    );
  }

  if (!details || details.Response === "False") {
    return <Container className="text-light">Detalhes não encontrados.</Container>;
  }
  return (
    <Container fluid className="text-white py-4 px-5 min-vh-100">
      <Button variant="link" onClick={handleBack} className="text-white mb-4">
        ← Voltar
      </Button>
  
      <div className="d-flex flex-column flex-md-row gap-4 align-items-start">
        <div className="flex-shrink-0" style={{ maxWidth: '350px' }}>
          <img
            src={details.Poster}
            alt={details.Title}
            className="img-fluid rounded shadow"
          />
        </div>
  
        <div className="flex-grow-1">
          <h1 className="display-5">{details.Title}</h1>
          <p className="fs-5 text-secondary mb-2">{details.Year}</p>
  
          <p><strong>Nota:</strong> {details.imdbRating}</p>
          <p><strong>Gênero:</strong> {details.Genre}</p>
          <p><strong>Diretor:</strong> {details.Director}</p>
          <p><strong>Elenco:</strong> {details.Actors}</p>
          <p><strong>Enredo:</strong> {details.Plot}</p>
        </div>
      </div>
    </Container>
  );  
}
