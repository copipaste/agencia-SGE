import { inject } from '@angular/core';
import { HttpLink } from 'apollo-angular/http';
import { InMemoryCache, ApolloLink } from '@apollo/client/core';
import { setContext } from '@apollo/client/link/context';
import { HttpHeaders } from '@angular/common/http';

const uri = 'https://agencia-backend-app.azurewebsites.net/graphql';

export function apolloOptionsFactory() {
  const httpLink = inject(HttpLink);
  
  const auth = setContext((operation, context) => {
    const token = localStorage.getItem('token');
    
    if (!token) {
      return {};
    }
    
    return {
      headers: new HttpHeaders({
        'Authorization': `Bearer ${token}`
      })
    };
  });

  const link = ApolloLink.from([auth, httpLink.create({ uri })]);
  const cache = new InMemoryCache();

  return {
    link,
    cache
  };
}
