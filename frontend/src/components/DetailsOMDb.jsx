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
    <Container className="py-4 text-white">
      <Button variant="secondary" onClick={handleBack} className="mb-3">
        Voltar
      </Button>
      <Card className="bg-dark text-white">
        <Card.Img variant="top" src={details.Poster} />
        <Card.Body>
          <Card.Title>{details.Title}</Card.Title>
          <Card.Text>
            <strong>Ano:</strong> {details.Year}<br />
            <strong>Gênero:</strong> {details.Genre}<br />
            <strong>Diretor:</strong> {details.Director}<br />
            <strong>Elenco:</strong> {details.Actors}<br />
            <strong>Enredo:</strong> {details.Plot}
          </Card.Text>
        </Card.Body>
      </Card>
    </Container>
  );
}
