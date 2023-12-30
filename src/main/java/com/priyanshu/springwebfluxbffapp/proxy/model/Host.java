package com.priyanshu.springwebfluxbffapp.proxy.model;

public class Host {
   private String id;
   private String uri;

   public String getId() {
      return id;
   }

   public void setId(String id) {
      this.id = id;
   }

   public String getUri() {
      return uri;
   }

   public void setUri(String uri) {
      this.uri = uri;
   }

   @Override
   public String toString() {
      return "Host{" +
              "id='" + id + '\'' +
              ", uri='" + uri + '\'' +
              '}';
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (!(o instanceof Host host)) return false;

       if (!getId().equals(host.getId())) return false;
       return getUri().equals(host.getUri());
   }

   @Override
   public int hashCode() {
      int result = getId().hashCode();
      result = 31 * result + getUri().hashCode();
      return result;
   }
}
