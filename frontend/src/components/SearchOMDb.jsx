import React, { useState } from 'react';
import { Container, Row, Col, Card, Form, Button } from 'react-bootstrap';

const API_KEY = '2464f402';

export default function App() {
  const [search, setSearch] = useState('');
  const [results, setResults] = useState([]);
  const [type, setType] = useState('');
  const [year, setYear] = useState('');

  const fetchMovies = async () => {
    const url = new URLSearchParams({
        apikey: API_KEY,
        s: search,
    });
    
    if(type) url.append('type', type);
    if(year) url.append('year', year);

    const res = await fetch(`https://www.omdbapi.com/?${url.toString()}`);
    const data = await res.json();
    console.log(data);
    setResults(data.Search || []);
  };

  return (
    <Container className="py-4 bg-dark text-white min-vh-100">
      <Form
        onSubmit={(e) => {
          e.preventDefault();
          fetchMovies();
        }}
        className="d-flex mb-4"
      >
        <Form.Control
          type="text"
          placeholder="Digite um título"
          value={search}
          onChange={(e) => setSearch(e.target.value)}
          className="me-2"
        />
        <Form.Select value={type} onChange={(e) => setType(e.target.value)}>
            <option value="">Tudo</option>
            <option value="movie">Filmes</option>
            <option value="series">Serie</option>
            <option value="episode">Episodio</option>
        </Form.Select>
        <Form.Control
            type='number'
            placeholder='Ano'
            value={year}
            onChange={(e)=> setYear(e.target.value)}
        />
        <Button type="submit" variant="primary">
          Buscar
        </Button>
      </Form>

      <Row>
        {results.map((movie) => (
          <Col key={movie.imdbID} sm={6} md={4} lg={3} className="mb-4">
            <Card className="h-100 bg-secondary text-white">
              <Card.Img variant="top" src={movie.Poster} />
              <Card.Body>
                <Card.Title>{movie.Title}</Card.Title>
                <Card.Text>{movie.Year}</Card.Text>
              </Card.Body>
            </Card>
          </Col>
        ))}
      </Row>
    </Container>
  );
}
